package uk.gov.ida.saml.security;

import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.metadata.resolver.impl.BasicRoleDescriptorResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.security.impl.MetadataCredentialResolver;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.signature.support.SignatureException;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.security.saml.MetadataFactory;
import uk.gov.ida.saml.security.saml.TestCredentialFactory;
import uk.gov.ida.saml.security.saml.builders.EntitiesDescriptorBuilder;
import uk.gov.ida.saml.security.saml.builders.EntityDescriptorBuilder;
import uk.gov.ida.saml.security.saml.builders.KeyDescriptorBuilder;
import uk.gov.ida.saml.security.saml.builders.SPSSODescriptorBuilder;

import java.security.PublicKey;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static uk.gov.ida.saml.core.test.TestEntityIds.HUB_ENTITY_ID;
import static uk.gov.ida.saml.core.test.TestEntityIds.STUB_IDP_ONE;

public class MetadataBackedEncryptionCredentialResolverTest {
    private MetadataCredentialResolver metadataCredentialResolver;

    @Before
    public void beforeAll() throws Exception {
        InitializationService.initialize();

        StringBackedMetadataResolver metadataResolver = new StringBackedMetadataResolver(loadMetadata());
        BasicParserPool basicParserPool = new BasicParserPool();
        basicParserPool.initialize();
        metadataResolver.setParserPool(basicParserPool);
        metadataResolver.setRequireValidMetadata(true);
        metadataResolver.setId("arbitrary id");
        metadataResolver.initialize();

        BasicRoleDescriptorResolver basicRoleDescriptorResolver = new BasicRoleDescriptorResolver(metadataResolver);
        basicRoleDescriptorResolver.initialize();

        metadataCredentialResolver = new MetadataCredentialResolver();
        metadataCredentialResolver.setRoleDescriptorResolver(basicRoleDescriptorResolver);
        metadataCredentialResolver.setKeyInfoCredentialResolver(DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver());
        metadataCredentialResolver.initialize();
    }

    private String loadMetadata() {
        final SPSSODescriptor spssoDescriptor = SPSSODescriptorBuilder.anSpServiceDescriptor()
                .addSupportedProtocol("urn:oasis:names:tc:SAML:2.0:protocol")
                .withoutDefaultSigningKey()
                .withoutDefaultEncryptionKey()
                .addKeyDescriptor(KeyDescriptorBuilder.aKeyDescriptor()
                        .withX509ForSigning(TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT).build())
                .addKeyDescriptor(KeyDescriptorBuilder.aKeyDescriptor()
                        .withX509ForSigning(TestCertificateStrings.METADATA_SIGNING_B_PUBLIC_CERT).build())
                .addKeyDescriptor(KeyDescriptorBuilder.aKeyDescriptor()
                        .withX509ForEncryption(TestCertificateStrings.HUB_TEST_PUBLIC_ENCRYPTION_CERT).build())
                .build();

        try {
            final EntityDescriptor entityDescriptor = EntityDescriptorBuilder.anEntityDescriptor()
                    .withId("0a2bf940-e6fe-4f32-833d-022dfbfc77c5")
                    .withEntityId(HUB_ENTITY_ID)
                    .withValidUntil(DateTime.now().plusYears(100))
                    .withCacheDuration(6000000L)
                    .addSpServiceDescriptor(spssoDescriptor)
                    .build();

            return new MetadataFactory().metadata(EntitiesDescriptorBuilder.anEntitiesDescriptor()
                    .withEntityDescriptors(Collections.singletonList(entityDescriptor)).build());
        } catch (MarshallingException | SignatureException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Test
    public void shouldSupportResolvingCredentialsFromKeysInMetadata() throws Exception {
        PublicKey publicKey = TestCredentialFactory.createPublicKey(TestCertificateStrings.HUB_TEST_PUBLIC_ENCRYPTION_CERT);
        assertThat(new MetadataBackedEncryptionCredentialResolver(metadataCredentialResolver, SPSSODescriptor.DEFAULT_ELEMENT_NAME).getEncryptingCredential(HUB_ENTITY_ID).getPublicKey()).isEqualTo(publicKey);
    }

    @Test
    public void shouldFailToResolveAndThrowIfEntityIsNotFound() throws Exception {
        assertThatThrownBy(() -> {
            new MetadataBackedEncryptionCredentialResolver(metadataCredentialResolver, IDPSSODescriptor.DEFAULT_ELEMENT_NAME).getEncryptingCredential(HUB_ENTITY_ID);
        }).isExactlyInstanceOf(MetadataBackedEncryptionCredentialResolver.CredentialMissingInMetadataException.class)
          .hasMessage("No public key for entity-id: \""+ HUB_ENTITY_ID + "\" could be found in the metadata. Metadata could be expired, invalid, or missing entities");
        assertThatThrownBy(() -> {
            new MetadataBackedEncryptionCredentialResolver(metadataCredentialResolver, IDPSSODescriptor.DEFAULT_ELEMENT_NAME).getEncryptingCredential(STUB_IDP_ONE);
        }).isExactlyInstanceOf(MetadataBackedEncryptionCredentialResolver.CredentialMissingInMetadataException.class)
          .hasMessage("No public key for entity-id: \""+ STUB_IDP_ONE + "\" could be found in the metadata. Metadata could be expired, invalid, or missing entities");
    }
}

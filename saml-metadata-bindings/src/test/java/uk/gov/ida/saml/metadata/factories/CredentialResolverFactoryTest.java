package uk.gov.ida.saml.metadata.factories;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.security.impl.MetadataCredentialResolver;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.core.test.TestEntityIds;
import uk.gov.ida.saml.metadata.test.factories.metadata.EntityDescriptorFactory;
import uk.gov.ida.saml.metadata.test.factories.metadata.TestCredentialFactory;

import java.security.PublicKey;

public class CredentialResolverFactoryTest {

    private static DOMMetadataResolver metadataResolver;

    @BeforeClass
    public static void beforeAll() throws Exception {
        InitializationService.initialize();

        //has Hub's entity ID
        EntityDescriptor entityDescriptor = new EntityDescriptorFactory().hubEntityDescriptor();
        Element element = XMLObjectSupport.marshall(entityDescriptor);

        metadataResolver = new DOMMetadataResolver(element);
        metadataResolver.setId("test-metadata-resolver");
        metadataResolver.initialize();
    }

    @Test
    public void shouldSupportResolvingCredentialsFromKeysInMetadata() throws Exception {
        MetadataCredentialResolver metadataCredentialResolver = new CredentialResolverFactory().create(metadataResolver);
        CriteriaSet trustBasisCriteria = new CriteriaSet();
        trustBasisCriteria.add(new EntityIdCriterion(TestEntityIds.HUB_ENTITY_ID));
        trustBasisCriteria.add(new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME));
        trustBasisCriteria.add(new UsageCriterion(UsageType.ENCRYPTION));

        PublicKey publicKey = TestCredentialFactory.createPublicKey(TestCertificateStrings.HUB_TEST_PUBLIC_ENCRYPTION_CERT);
        Assertions.assertThat(metadataCredentialResolver.resolveSingle(trustBasisCriteria).getPublicKey()).isEqualTo(publicKey);
    }

    @Test
    public void shouldFailToResolveIfEnttiyIsNotFound() throws Exception {
        MetadataCredentialResolver metadataCredentialResolver = new CredentialResolverFactory().create(metadataResolver);

        CriteriaSet trustBasisCriteria = new CriteriaSet();
        trustBasisCriteria.add(new EntityIdCriterion(TestEntityIds.STUB_IDP_ONE));
        trustBasisCriteria.add(new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME));

        Assertions.assertThat(metadataCredentialResolver.resolveSingle(trustBasisCriteria)).isNull();
    }

}
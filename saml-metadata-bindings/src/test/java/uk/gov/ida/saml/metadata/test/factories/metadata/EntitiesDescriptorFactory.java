package uk.gov.ida.saml.metadata.test.factories.metadata;

import com.google.common.base.Throwables;
import org.joda.time.DateTime;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import uk.gov.ida.saml.core.test.builders.metadata.EntitiesDescriptorBuilder;
import uk.gov.ida.saml.core.test.builders.metadata.SignatureBuilder;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static uk.gov.ida.saml.core.test.TestCertificateStrings.METADATA_SIGNING_A_PRIVATE_KEY;
import static uk.gov.ida.saml.core.test.TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT;
import static uk.gov.ida.saml.core.test.builders.metadata.EntitiesDescriptorBuilder.anEntitiesDescriptor;

public class EntitiesDescriptorFactory {
    private final DateTime validUntil = DateTime.now().plusWeeks(2);
    private EntityDescriptorFactory entityDescriptorFactory = new EntityDescriptorFactory();

    public EntitiesDescriptor defaultEntitiesDescriptor() {
        return entitiesDescriptor(defaultEntityDescriptors());
    }

    public EntitiesDescriptor emptyEntitiesDescriptor() {
        return entitiesDescriptor(emptyList());
    }

    public EntitiesDescriptor entitiesDescriptor(List<EntityDescriptor> entityDescriptors) {
        return buildEntitiesDescriptor(entityDescriptors, Optional.of(defaultSignature()), validUntil);
    }

    public EntitiesDescriptor expiredEntitiesDescriptor() {
        DateTime expired = DateTime.now().minusWeeks(2);
        return buildEntitiesDescriptor(defaultEntityDescriptors(), Optional.of(defaultSignature()), expired);
    }

    public EntitiesDescriptor unsignedEntitiesDescriptor() {
        return buildEntitiesDescriptor(defaultEntityDescriptors(), Optional.empty(), validUntil);
    }

    private EntitiesDescriptor buildEntitiesDescriptor(List<EntityDescriptor> entityDescriptors, Optional<Signature> signature, DateTime validUntil) {
        EntitiesDescriptorBuilder entitiesDescriptorBuilder = entitiesDescriptorBuilder(entityDescriptors, signature, validUntil);
        return buildEntitiesDescriptor(entitiesDescriptorBuilder);
    }

    private EntitiesDescriptor buildEntitiesDescriptor(EntitiesDescriptorBuilder entitiesDescriptorBuilder) {
        try {
            return entitiesDescriptorBuilder.build();
        } catch (MarshallingException | SignatureException e) {
            throw Throwables.propagate(e);
        }
    }

    private EntitiesDescriptorBuilder entitiesDescriptorBuilder(List<EntityDescriptor> entityDescriptors, Optional<Signature> signature, DateTime validUntil) {
        EntitiesDescriptorBuilder entitiesDescriptorBuilder = anEntitiesDescriptor()
                .withEntityDescriptors(entityDescriptors)
                .withValidUntil(validUntil);
        signature.ifPresent(entitiesDescriptorBuilder::withSignature);
        return entitiesDescriptorBuilder;
    }

    private List<EntityDescriptor> defaultEntityDescriptors() {
        return entityDescriptorFactory.defaultEntityDescriptors();
    }

    private Signature defaultSignature() {
        return buildSignature(METADATA_SIGNING_A_PUBLIC_CERT, METADATA_SIGNING_A_PRIVATE_KEY);
    }

    private Signature buildSignature(String publicCertificate, String privateKey) {
        TestCredentialFactory testCredentialFactory = new TestCredentialFactory(publicCertificate, privateKey);
        Credential credential = testCredentialFactory.getSigningCredential();
        return SignatureBuilder.aSignature().withSigningCredential(credential).withX509Data(publicCertificate).build();
    }

    private Signature buildFullChainSignature(String publicCertificate, List<String> certificateChain, String privateKey) {
        TestCredentialFactory testCredentialFactory = new TestCredentialFactory(publicCertificate, privateKey);
        Credential credential = testCredentialFactory.getSigningCredential();
        return SignatureBuilder.aSignature().withSigningCredential(credential).withX509Data(certificateChain).build();
    }

    public EntitiesDescriptor signedEntitiesDescriptor(String publicCertificate, String privateKey) {
        return buildEntitiesDescriptor(defaultEntityDescriptors(), Optional.of(buildSignature(publicCertificate, privateKey)), validUntil);
    }

    public EntitiesDescriptor signedEntitiesDescriptor(Signature signature) {
        return buildEntitiesDescriptor(defaultEntityDescriptors(), Optional.of(signature), validUntil);
    }

    public EntitiesDescriptor signedEntitiesDescriptor(String id, Signature signature) {
        EntitiesDescriptorBuilder entitiesDescriptorBuilder = entitiesDescriptorBuilder(defaultEntityDescriptors(), Optional.of(signature), validUntil);
        entitiesDescriptorBuilder.withId(id);
        return buildEntitiesDescriptor(entitiesDescriptorBuilder);
    }

    public EntitiesDescriptor fullChainSignedEntitiesDescriptor(String publicCertificate, List<String> certificateChain, String privateKey) {
        return buildEntitiesDescriptor(defaultEntityDescriptors(), Optional.of(buildFullChainSignature(publicCertificate, certificateChain, privateKey)), validUntil);
    }
}

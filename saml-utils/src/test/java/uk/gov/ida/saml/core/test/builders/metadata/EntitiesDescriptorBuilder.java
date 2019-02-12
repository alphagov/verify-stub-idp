package uk.gov.ida.saml.core.test.builders.metadata;

import org.joda.time.DateTime;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntitiesDescriptorBuilder {
    private String name = "VERIFY-FEDERATION";
    private List<EntityDescriptor> entityDescriptors = new ArrayList<>();
    private Optional<Signature> signature = Optional.empty();
    private String id = UUID.randomUUID().toString();
    private Optional<DateTime> validUntil = Optional.empty();
    private Optional<Long> cacheDuration = Optional.empty();

    public static EntitiesDescriptorBuilder anEntitiesDescriptor() {
        return new EntitiesDescriptorBuilder();
    }

    public EntitiesDescriptorBuilder withEntityDescriptors(List<EntityDescriptor> entityDescriptors) {
        this.entityDescriptors = entityDescriptors;
        return this;
    }

    public EntitiesDescriptorBuilder withSignature(Signature signature) {
        this.signature = Optional.of(signature);
        return this;
    }

    public EntitiesDescriptorBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public EntitiesDescriptorBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public EntitiesDescriptorBuilder withValidUntil(DateTime validUntil) {
        this.validUntil = Optional.of(validUntil);
        return this;
    }

    public EntitiesDescriptorBuilder withCacheDuration(Long cacheDuration) {
        this.cacheDuration = Optional.of(cacheDuration);
        return this;
    }

    public EntitiesDescriptorBuilder() {
    }

    public EntitiesDescriptor build() throws MarshallingException, SignatureException {
        EntitiesDescriptor entitiesDescriptor = new org.opensaml.saml.saml2.metadata.impl.EntitiesDescriptorBuilder().buildObject();

        entitiesDescriptor.getEntityDescriptors().addAll(entityDescriptors);
        entitiesDescriptor.setID(id);
        entitiesDescriptor.setName(name);
        validUntil.ifPresent(entitiesDescriptor::setValidUntil);
        cacheDuration.ifPresent(entitiesDescriptor::setCacheDuration);

        if (signature.isPresent()) {
            entitiesDescriptor.setSignature(signature.get());
            XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(entitiesDescriptor).marshall(entitiesDescriptor);
            Signer.signObject(entitiesDescriptor.getSignature());
        }
        return entitiesDescriptor;
    }
}

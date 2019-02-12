package uk.gov.ida.saml.core.test.builders.metadata;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;

import java.util.Optional;

public class AssertionConsumerServiceBuilder {

    private Optional<Integer> index = Optional.ofNullable(1);
    private Optional<String> binding = Optional.ofNullable(SAMLConstants.SAML2_POST_BINDING_URI);
    private Optional<String> location = Optional.ofNullable("http://foo.com/bar");
    private boolean isDefault = false;

    public static AssertionConsumerServiceBuilder anAssertionConsumerService() {
        return new AssertionConsumerServiceBuilder();
    }

    public AssertionConsumerService build() {
        AssertionConsumerService assertionConsumerService = new org.opensaml.saml.saml2.metadata.impl.AssertionConsumerServiceBuilder().buildObject();
        assertionConsumerService.setBinding(binding.orElse(null));
        assertionConsumerService.setLocation(location.orElse(null));
        assertionConsumerService.setIndex(index.orElse(null));
        assertionConsumerService.setIsDefault(isDefault);
        return assertionConsumerService;
    }

    public AssertionConsumerServiceBuilder withBinding(String binding) {
        this.binding = Optional.ofNullable(binding);
        return this;
    }

    public AssertionConsumerServiceBuilder withLocation(String location) {
        this.location = Optional.ofNullable(location);
        return this;
    }

    public AssertionConsumerServiceBuilder withIndex(Integer index) {
        this.index = Optional.ofNullable(index);
        return this;
    }

    public AssertionConsumerServiceBuilder isDefault() {
        this.isDefault = true;
        return this;
    }
}

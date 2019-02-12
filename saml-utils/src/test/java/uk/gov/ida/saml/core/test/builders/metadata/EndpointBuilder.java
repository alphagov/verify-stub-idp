package uk.gov.ida.saml.core.test.builders.metadata;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml.saml2.metadata.impl.SingleSignOnServiceBuilder;

public class EndpointBuilder {

    private String binding = SAMLConstants.SAML2_POST_BINDING_URI;
    private String location = "http://foo.com/bar";

    public static EndpointBuilder anEndpoint() {
        return new EndpointBuilder();
    }

    public SingleSignOnService buildSingleSignOnService() {
        SingleSignOnService singleSignOnService = new SingleSignOnServiceBuilder().buildObject();
        if (location != null) {
            singleSignOnService.setLocation(location);
        }
        if (binding != null) {
            singleSignOnService.setBinding(binding);
        }
        return singleSignOnService;
    }

    public EndpointBuilder withBinding(String binding) {
        this.binding = binding;
        return this;
    }

    public EndpointBuilder withLocation(String location) {
        this.location = location;
        return this;
    }
}

package uk.gov.ida.saml.core.test.builders.metadata;

import org.opensaml.saml.saml2.metadata.EmailAddress;

import java.net.URI;

public class EmailAddressBuilder {
    private URI value = URI.create("mailto:fred@flintstone.com");

    public static EmailAddressBuilder anEmailAddress() {
        return new EmailAddressBuilder();
    }

    public EmailAddress build() {
        EmailAddress emailAddress = new org.opensaml.saml.saml2.metadata.impl.EmailAddressBuilder().buildObject();
        if (value != null) {
            emailAddress.setAddress(value.toString());
        }
        return emailAddress;
    }

    public EmailAddressBuilder withAddress(URI address) {
        this.value = address;
        return this;
    }
}

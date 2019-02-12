package uk.gov.ida.saml.core.test.builders.metadata;

import org.opensaml.saml.saml2.metadata.GivenName;

import java.util.Optional;

public class GivenNameBuilder {
    private Optional<String> value = Optional.ofNullable("Fred");

    public static GivenNameBuilder aGivenName(){
        return new GivenNameBuilder();
    }

    public GivenName build() {
        GivenName givenName = new org.opensaml.saml.saml2.metadata.impl.GivenNameBuilder().buildObject();
        givenName.setName(value.get());
        return givenName;
    }
}

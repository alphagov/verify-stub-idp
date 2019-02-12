package uk.gov.ida.saml.core.test.builders.metadata;

import org.opensaml.saml.saml2.metadata.SurName;

import java.util.Optional;

public class SurNameBuilder {
    private Optional<String> name = Optional.ofNullable("Flintstone");

    public static SurNameBuilder aSurName(){
        return new SurNameBuilder();
    }

    public SurName build() {
        SurName name = new org.opensaml.saml.saml2.metadata.impl.SurNameBuilder().buildObject();
        name.setName(this.name.get());
        return name;
    }
}

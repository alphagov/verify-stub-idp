package uk.gov.ida.saml.core.test.builders;

import java.util.Optional;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.NameIDType;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;

public class NameIdPolicyBuilder {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
    private Optional<String> format = Optional.ofNullable(NameIDType.PERSISTENT);

    public static NameIdPolicyBuilder aNameIdPolicy() {
        return new NameIdPolicyBuilder();
    }

    public NameIDPolicy build() {

        NameIDPolicy nameIdPolicy = openSamlXmlObjectFactory.createNameIdPolicy();

        if (format.isPresent()) {
            nameIdPolicy.setFormat(format.get());
        }

        return nameIdPolicy;
    }

    public NameIdPolicyBuilder withFormat(String format) {
        this.format = Optional.ofNullable(format);
        return this;
    }
}

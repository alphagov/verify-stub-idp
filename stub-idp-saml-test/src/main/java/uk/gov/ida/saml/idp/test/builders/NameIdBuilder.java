package uk.gov.ida.saml.idp.test.builders;

import java.util.Optional;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDType;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;

public class NameIdBuilder {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
    private String value;
    private Optional<String> format = Optional.ofNullable(NameIDType.PERSISTENT);
    private Optional<String> nameQualifier = Optional.empty();
    private Optional<String> spNameQualifier = Optional.empty();

    public static NameIdBuilder aNameId() {
        return new NameIdBuilder();
    }

    public NameID build() {
        NameID nameId = openSamlXmlObjectFactory.createNameId(value);
        nameId.setFormat(null);

        if (format.isPresent()) {
            nameId.setFormat(format.get());
        }

        if (nameQualifier.isPresent()) {
            nameId.setNameQualifier(nameQualifier.get());
        }

        if (spNameQualifier.isPresent()) {
            nameId.setSPNameQualifier(spNameQualifier.get());
        }

        return nameId;
    }

    public NameIdBuilder withValue(String value) {
        this.value = value;
        return this;
    }

    public NameIdBuilder withFormat(String format) {
        this.format = Optional.ofNullable(format);
        return this;
    }

    public NameIdBuilder withNameQualifier(String nameQualifier) {
        this.nameQualifier = Optional.ofNullable(nameQualifier);
        return this;
    }

    public NameIdBuilder withSpNameQualifier(String spNameQualifier) {
        this.spNameQualifier = Optional.ofNullable(spNameQualifier);
        return this;
    }
}

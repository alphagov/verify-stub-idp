package uk.gov.ida.saml.idp.test.builders;

import java.util.Optional;
import org.opensaml.saml.saml2.core.Attribute;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.extensions.StringBasedMdsAttributeValue;

public class SimpleStringAttributeBuilder {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
    private Optional<String> name = Optional.empty();
    private Optional<String> simpleStringValue = Optional.empty();

    public static SimpleStringAttributeBuilder aSimpleStringAttribute() {
        return new SimpleStringAttributeBuilder();
    }

    public Attribute build() {
        Attribute attribute = openSamlXmlObjectFactory.createAttribute();

        name.ifPresent(attribute::setName);
        if (simpleStringValue.isPresent()){
            StringBasedMdsAttributeValue attributeValue = openSamlXmlObjectFactory.createSimpleMdsAttributeValue(simpleStringValue.get());
            attribute.getAttributeValues().add(attributeValue);
        }

        return attribute;
    }

    public SimpleStringAttributeBuilder withName(String name) {
        this.name = Optional.ofNullable(name);
        return this;
    }

    public SimpleStringAttributeBuilder withSimpleStringValue(String value){
        this.simpleStringValue = Optional.ofNullable(value);
        return this;
    }
}

package uk.gov.ida.saml.idp.test.builders;

import com.google.common.base.Optional;
import org.opensaml.saml.saml2.core.Attribute;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.extensions.StringBasedMdsAttributeValue;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;

public class SimpleStringAttributeBuilder {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
    private Optional<String> name = Optional.absent();
    private Optional<String> simpleStringValue = absent();

    public static SimpleStringAttributeBuilder aSimpleStringAttribute() {
        return new SimpleStringAttributeBuilder();
    }

    public Attribute build() {
        Attribute attribute = openSamlXmlObjectFactory.createAttribute();

        if (name.isPresent()) {
            attribute.setName(name.get());
        }
        if (simpleStringValue.isPresent()){
            StringBasedMdsAttributeValue attributeValue = openSamlXmlObjectFactory.createSimpleMdsAttributeValue(simpleStringValue.get());
            attribute.getAttributeValues().add(attributeValue);
        }

        return attribute;
    }

    public SimpleStringAttributeBuilder withName(String name) {
        this.name = fromNullable(name);
        return this;
    }

    public SimpleStringAttributeBuilder withSimpleStringValue(String value){
        this.simpleStringValue = fromNullable(value);
        return this;
    }
}

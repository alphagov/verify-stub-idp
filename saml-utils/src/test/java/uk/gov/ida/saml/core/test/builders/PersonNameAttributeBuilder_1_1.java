package uk.gov.ida.saml.core.test.builders;

import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeValue;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.test.OpenSamlXmlObjectFactory;

import java.util.ArrayList;
import java.util.List;

public class PersonNameAttributeBuilder_1_1 {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();

    private List<AttributeValue> values = new ArrayList<>();
    private String nameFormat = Attribute.UNSPECIFIED;
    private boolean addDefaultValue = true;
    private AttributeValue defaultPersonNameAttributeValue = PersonNameAttributeValueBuilder.aPersonNameValue().build();

    public static PersonNameAttributeBuilder_1_1 aPersonName_1_1() {
        return new PersonNameAttributeBuilder_1_1();
    }

    public Attribute buildAsFirstname() {
        Attribute attribute = build();

        attribute.setFriendlyName(IdaConstants.Attributes_1_1.Firstname.FRIENDLY_NAME);
        attribute.setName(IdaConstants.Attributes_1_1.Firstname.NAME);

        return attribute;
    }

    public Attribute buildAsSurname() {
        Attribute attribute = build();

        attribute.setFriendlyName(IdaConstants.Attributes_1_1.Surname.FRIENDLY_NAME);
        attribute.setName(IdaConstants.Attributes_1_1.Surname.NAME);

        return attribute;
    }

    public Attribute buildAsMiddlename() {
        Attribute attribute = build();

        attribute.setFriendlyName(IdaConstants.Attributes_1_1.Middlename.FRIENDLY_NAME);
        attribute.setName(IdaConstants.Attributes_1_1.Middlename.NAME);

        return attribute;
    }

    public Attribute buildAsEidasFirstname() {
        Attribute attribute = build();

        attribute.setFriendlyName(IdaConstants.Eidas_Attributes.FirstName.FRIENDLY_NAME);
        attribute.setName(IdaConstants.Eidas_Attributes.FirstName.NAME);

        return attribute;
    }

    public Attribute buildAsEidasFamilyName() {
        Attribute attribute = build();

        attribute.setFriendlyName(IdaConstants.Eidas_Attributes.FamilyName.FRIENDLY_NAME);
        attribute.setName(IdaConstants.Eidas_Attributes.FamilyName.NAME);

        return attribute;
    }

    private Attribute build() {
        Attribute personNameAttribute = openSamlXmlObjectFactory.createAttribute();
        personNameAttribute.setNameFormat(nameFormat);
        if (addDefaultValue) {
            this.values.add(defaultPersonNameAttributeValue);
        }

        for (AttributeValue value : values) {
            personNameAttribute.getAttributeValues().add(value);
        }
        return personNameAttribute;
    }

    public Attribute buildAsFirstnameWithNoAttributeValues() {
        Attribute attribute = openSamlXmlObjectFactory.createAttribute();
        attribute.setNameFormat(nameFormat);
        attribute.setFriendlyName(IdaConstants.Attributes_1_1.Firstname.FRIENDLY_NAME);
        attribute.setName(IdaConstants.Attributes_1_1.Firstname.NAME);
        return attribute;
    }

    public PersonNameAttributeBuilder_1_1 addValue(AttributeValue attributeValue) {
        this.values.add(attributeValue);
        this.addDefaultValue = false;
        return this;
    }

    public PersonNameAttributeBuilder_1_1 withNameFormat(String nameFormat) {
        this.nameFormat = nameFormat;
        return this;
    }

}

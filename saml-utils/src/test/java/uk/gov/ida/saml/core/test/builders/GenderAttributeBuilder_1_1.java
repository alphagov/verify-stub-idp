package uk.gov.ida.saml.core.test.builders;

import org.joda.time.DateTime;
import org.opensaml.saml.saml2.core.Attribute;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.extensions.Gender;
import uk.gov.ida.saml.core.test.OpenSamlXmlObjectFactory;

import java.util.Optional;


public class GenderAttributeBuilder_1_1 {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();

    private Optional<DateTime> from = Optional.empty();
    private Optional<DateTime> to = Optional.empty();
    private Optional<String> value = Optional.empty();
    private boolean verified = false;

    public static GenderAttributeBuilder_1_1 aGender_1_1() {
        return new GenderAttributeBuilder_1_1();
    }

    public Attribute build() {

        Attribute genderAttribute = openSamlXmlObjectFactory.createAttribute();
        genderAttribute.setFriendlyName(IdaConstants.Attributes_1_1.Gender.FRIENDLY_NAME);
        genderAttribute.setName(IdaConstants.Attributes_1_1.Gender.NAME);
        genderAttribute.setNameFormat(Attribute.UNSPECIFIED);

        return buildAttribute(genderAttribute);
    }

    public Attribute buildEidasGender() {

        Attribute genderAttribute = openSamlXmlObjectFactory.createAttribute();
        genderAttribute.setFriendlyName(IdaConstants.Eidas_Attributes.Gender.FRIENDLY_NAME);
        genderAttribute.setName(IdaConstants.Eidas_Attributes.Gender.NAME);
        genderAttribute.setNameFormat(Attribute.UNSPECIFIED);

        return buildAttribute(genderAttribute);
    }

    private Attribute buildAttribute(Attribute genderAttribute) {
        Gender genderAttributeValue = openSamlXmlObjectFactory.createGenderAttributeValue(value.orElse("Male"));

        from.ifPresent(genderAttributeValue::setFrom);
        to.ifPresent(genderAttributeValue::setTo);

        genderAttributeValue.setVerified(verified);

        genderAttribute.getAttributeValues().add(genderAttributeValue);

        return genderAttribute;
    }

    public GenderAttributeBuilder_1_1 withFrom(DateTime from) {
        this.from = Optional.ofNullable(from);
        return this;
    }

    public GenderAttributeBuilder_1_1 withTo(DateTime to) {
        this.to = Optional.ofNullable(to);
        return this;
    }

    public GenderAttributeBuilder_1_1 withValue(String name) {
        this.value = Optional.ofNullable(name);
        return this;
    }

    public GenderAttributeBuilder_1_1 withVerified(boolean verified) {
        this.verified = verified;
        return this;
    }
}

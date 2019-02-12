package uk.gov.ida.saml.core.test.builders;

import org.joda.time.DateTime;
import org.opensaml.saml.saml2.core.AttributeValue;
import uk.gov.ida.saml.core.extensions.PersonName;
import uk.gov.ida.saml.core.test.OpenSamlXmlObjectFactory;

import java.util.Optional;

public class PersonNameAttributeValueBuilder {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();

    private Optional<DateTime> from = Optional.empty();
    private Optional<DateTime> to = Optional.empty();
    private String value = "John";
    private Optional<String> language = Optional.empty();
    private Optional<Boolean> verified = Optional.empty();

    public static PersonNameAttributeValueBuilder aPersonNameValue() {
        return new PersonNameAttributeValueBuilder();
    }

    public AttributeValue build() {
        PersonName personNameAttributeValue = openSamlXmlObjectFactory.createPersonNameAttributeValue(value);

        from.ifPresent(personNameAttributeValue::setFrom);
        to.ifPresent(personNameAttributeValue::setTo);
        verified.ifPresent(personNameAttributeValue::setVerified);
        language.ifPresent(personNameAttributeValue::setLanguage);

        return personNameAttributeValue;
    }

    public PersonNameAttributeValueBuilder withFrom(DateTime from) {
        this.from = Optional.ofNullable(from);
        return this;
    }

    public PersonNameAttributeValueBuilder withTo(DateTime to) {
        this.to = Optional.ofNullable(to);
        return this;
    }

    public PersonNameAttributeValueBuilder withValue(String name) {
        this.value = name;
        return this;
    }

    public PersonNameAttributeValueBuilder withVerified(Boolean verified) {
        this.verified = Optional.ofNullable(verified);
        return this;
    }
}

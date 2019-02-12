package uk.gov.ida.saml.core.test.builders;

import org.joda.time.DateTime;
import org.opensaml.saml.saml2.core.AttributeValue;
import uk.gov.ida.saml.core.extensions.Date;
import uk.gov.ida.saml.core.test.OpenSamlXmlObjectFactory;

import java.util.Optional;

public class DateAttributeValueBuilder {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();

    private Optional<DateTime> from = Optional.empty();
    private Optional<DateTime> to = Optional.empty();
    private String value = "1991-04-12";
    private Optional<Boolean> verified = Optional.empty();

    public static DateAttributeValueBuilder aDateValue() {
        return new DateAttributeValueBuilder();
    }

    public AttributeValue build() {
        Date dateAttributeValue = openSamlXmlObjectFactory.createDateAttributeValue(value);

        if (from.isPresent()) {
            dateAttributeValue.setFrom(from.get());
        }
        if (to.isPresent()) {
            dateAttributeValue.setTo(to.get());
        }
        if (verified.isPresent()) {
            dateAttributeValue.setVerified(verified.get());
        }
        return dateAttributeValue;
    }

    public DateAttributeValueBuilder withFrom(DateTime from) {
        this.from = Optional.ofNullable(from);
        return this;
    }

    public DateAttributeValueBuilder withTo(DateTime to) {
        this.to = Optional.ofNullable(to);
        return this;
    }

    public DateAttributeValueBuilder withValue(String name) {
        this.value = name;
        return this;
    }

    public DateAttributeValueBuilder withVerified(Boolean verified) {
        this.verified = Optional.ofNullable(verified);
        return this;
    }
}

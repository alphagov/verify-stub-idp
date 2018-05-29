package uk.gov.ida.saml.idp.builders;

import org.joda.time.DateTime;
import uk.gov.ida.saml.core.domain.TransliterableMdsValue;

public class TransliterableMdsValueBuilder {

    private String value = null;

    private DateTime from = DateTime.now().minusDays(5);
    private DateTime to = DateTime.now().plusDays(5);
    private boolean verified = false;

    public static TransliterableMdsValueBuilder asTransliterableMdsValue() {
        return new TransliterableMdsValueBuilder();
    }

    public TransliterableMdsValue build() {
        return new TransliterableMdsValue(SimpleMdsValueBuilder.<String>aSimpleMdsValue()
                .withValue(value)
                .withFrom(from)
                .withTo(to)
                .withVerifiedStatus(verified)
                .build());
    }

    public TransliterableMdsValueBuilder withValue(String value) {
        this.value = value;
        return this;
    }

    public TransliterableMdsValueBuilder withFrom(DateTime from) {
        this.from = from;
        return this;
    }

    public TransliterableMdsValueBuilder withTo(DateTime to) {
        this.to = to;
        return this;
    }

    public TransliterableMdsValueBuilder withVerifiedStatus(boolean verified) {
        this.verified = verified;
        return this;
    }
}

package uk.gov.ida.saml.idp.test.builders;

import uk.gov.ida.saml.core.domain.FraudDetectedDetails;

public class FraudDetectedDetailsBuilder {

    private String eventId = "default-event-id";
    private String fraudIndicator = "IT01";

    public static FraudDetectedDetailsBuilder aFraudDetectedDetails() {
        return new FraudDetectedDetailsBuilder();
    }

    public FraudDetectedDetails build() {
        return new FraudDetectedDetails(eventId, fraudIndicator);
    }


    public FraudDetectedDetailsBuilder withFraudIndicator(String fraudIndicator) {
        this.fraudIndicator = fraudIndicator;
        return this;
    }

}

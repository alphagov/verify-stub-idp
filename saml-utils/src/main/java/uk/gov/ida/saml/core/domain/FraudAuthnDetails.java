package uk.gov.ida.saml.core.domain;

public class FraudAuthnDetails {

    private final String eventId;
    private final String fraudIndicator;

    public FraudAuthnDetails(String eventId, String fraudIndicator) {
        this.eventId = eventId;
        this.fraudIndicator = fraudIndicator;
    }

    public String getEventId() {
        return eventId;
    }

    public String getFraudIndicator() {
        return fraudIndicator;
    }
}

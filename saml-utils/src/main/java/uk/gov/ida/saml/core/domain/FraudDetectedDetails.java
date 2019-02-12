package uk.gov.ida.saml.core.domain;

public class FraudDetectedDetails {
    private String idpFraudEventId;
    private String fraudIndicator;

    public FraudDetectedDetails(String idpFraudEventId, String fraudIndicator) {
        this.idpFraudEventId = idpFraudEventId;
        this.fraudIndicator = fraudIndicator;
    }

    public String getIdpFraudEventId() {
        return idpFraudEventId;
    }

    public String getFraudIndicator() {
        return fraudIndicator;
    }
}

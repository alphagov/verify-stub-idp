package uk.gov.ida.common.shared.security;

public class Certificate {
    private String issuerId;
    private String certificate;
    private KeyUse keyUse;

    public Certificate(String issuerId, String certificate, KeyUse keyUse) {
        this.issuerId = issuerId;
        this.certificate = certificate;
        this.keyUse = keyUse;
    }

    public String getIssuerId() {
        return issuerId;
    }

    public String getCertificate() {
        return certificate;
    }

    public KeyUse getKeyUse() {
        return keyUse;
    }

    public enum KeyUse {
        Signing("SIGNING"),
        Encryption("ENCRYPTION");
        private String description;

        KeyUse(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    public static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
    public static final String END_CERT = "-----END CERTIFICATE-----";
}

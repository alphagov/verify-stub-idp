package uk.gov.ida.saml.metadata.exception;

public class EmptyTrustStoreException extends RuntimeException {
    public EmptyTrustStoreException() {
        super("TrustStore was empty");
    }
    public EmptyTrustStoreException(String message) {
        super(message);
    }
}

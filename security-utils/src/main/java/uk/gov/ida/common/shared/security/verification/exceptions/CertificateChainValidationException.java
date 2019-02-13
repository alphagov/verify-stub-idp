package uk.gov.ida.common.shared.security.verification.exceptions;

public class CertificateChainValidationException extends RuntimeException {
    public CertificateChainValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

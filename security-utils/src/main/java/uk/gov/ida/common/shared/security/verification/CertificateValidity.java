package uk.gov.ida.common.shared.security.verification;

import com.google.common.base.Optional;

import java.security.cert.CertPathValidatorException;

public class CertificateValidity {
    private final Optional<CertPathValidatorException> exception;

    public static CertificateValidity valid() {
        return new CertificateValidity(Optional.<CertPathValidatorException>absent());
    }

    public static CertificateValidity invalid(CertPathValidatorException e) {
        return new CertificateValidity(Optional.of(e));
    }

    private CertificateValidity(Optional<CertPathValidatorException> exception) {
        this.exception = exception;
    }

    public boolean isValid() {
        return !exception.isPresent();
    }

    public Optional<CertPathValidatorException> getException() {
        return exception;
    }

}

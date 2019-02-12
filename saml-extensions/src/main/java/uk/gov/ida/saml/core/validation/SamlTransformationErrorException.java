package uk.gov.ida.saml.core.validation;

import org.slf4j.event.Level;

public class SamlTransformationErrorException extends RuntimeException {

    private final Level logLevel;

    public SamlTransformationErrorException(
            String errorMessage,
            Exception cause,
            Level logLevel) {
        super(errorMessage, cause);
        this.logLevel = logLevel;
    }

    public SamlTransformationErrorException(
            String errorMessage,
            Level logLevel) {
        this(errorMessage, null, logLevel);
    }

    public Level getLogLevel() {
        return logLevel;
    }
}

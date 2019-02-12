package uk.gov.ida.saml.core.validation;


import org.slf4j.event.Level;

public abstract class SamlValidationSpecificationFailure extends SamlValidationSpecification {

    private final Level logLevel;

    protected SamlValidationSpecificationFailure(String message, boolean contextExpected, Level logLevel) {
        super(message, contextExpected);
        this.logLevel = logLevel;
    }

    protected SamlValidationSpecificationFailure(String message, boolean contextExpected) {
        this(message, contextExpected, Level.ERROR);
    }

    public Level getLogLevel() {
        return logLevel;
    }
}

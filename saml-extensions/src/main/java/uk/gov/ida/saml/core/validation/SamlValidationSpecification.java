package uk.gov.ida.saml.core.validation;

import java.text.MessageFormat;

public abstract class SamlValidationSpecification {

    private String message;
    private boolean contextExpected;

    public abstract SamlDocumentReference documentReference();

    protected SamlValidationSpecification(String message, boolean contextExpected) {
        this.message = message;
        this.contextExpected = contextExpected;
    }

    public String getErrorMessage(){
        return MessageFormat.format("SAML Validation Specification: {0}\n{1}", message, documentReference());
    }

    public boolean isContextExpected() {
        return contextExpected;
    }
}

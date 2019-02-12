package uk.gov.ida.saml.core.validation;

public abstract class SamlValidationSpecificationWarning extends SamlValidationSpecification {
    protected SamlValidationSpecificationWarning(String message, Boolean contextExpected) {
        super(message, contextExpected);
    }
}

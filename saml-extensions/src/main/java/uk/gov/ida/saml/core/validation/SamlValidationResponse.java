package uk.gov.ida.saml.core.validation;

public final class SamlValidationResponse {
    private final SamlValidationSpecificationFailure samlValidationSpecificationFailure;
    private final Exception cause;
    private final boolean isOK;

    private SamlValidationResponse(
            final boolean isOK,
            final SamlValidationSpecificationFailure validationSpecification,
            final Exception cause) {

        this.samlValidationSpecificationFailure = validationSpecification;
        this.isOK = isOK;
        this.cause = cause;
    }


    public static SamlValidationResponse aValidResponse() {
        return new SamlValidationResponse(true, null, null);
    }

    public static SamlValidationResponse anInvalidResponse(
            final SamlValidationSpecificationFailure samlValidationSpecification) {

        return new SamlValidationResponse(false, samlValidationSpecification, null);
    }

    public static SamlValidationResponse anInvalidResponse(
            final SamlValidationSpecificationFailure samlValidationSpecification,
            final Exception cause) {

        return new SamlValidationResponse(false, samlValidationSpecification, cause);
    }

    public boolean isOK() {
        return isOK;
    }

    public String getErrorMessage() {return samlValidationSpecificationFailure.getErrorMessage();}

    public SamlValidationSpecificationFailure getSamlValidationSpecificationFailure() {return samlValidationSpecificationFailure;}

    public Exception getCause() {
        return cause;
    }
}

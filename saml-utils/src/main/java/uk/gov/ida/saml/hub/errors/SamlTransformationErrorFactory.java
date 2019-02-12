package uk.gov.ida.saml.hub.errors;

import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;
import uk.gov.ida.saml.core.validation.errors.ResponseProcessingValidationSpecification;
import uk.gov.ida.saml.core.validation.errors.StringValidationSpecification;

public final class SamlTransformationErrorFactory {

    private SamlTransformationErrorFactory() {
    }

    public static SamlValidationSpecificationFailure stringTooSmall(int length, int lowerBound) {
        return new StringValidationSpecification(StringValidationSpecification.LOWER_BOUND_ERROR_MESSAGE, length, lowerBound);
    }

    public static SamlValidationSpecificationFailure stringTooLarge(int length, int upperBound) {
        return new StringValidationSpecification(StringValidationSpecification.UPPER_BOUND_ERROR_MESSAGE, length, upperBound);
    }

    public static SamlValidationSpecificationFailure missingAttributeStatementInAssertion(final String assertionId) {
        return new ResponseProcessingValidationSpecification(ResponseProcessingValidationSpecification.MISSING_ATTRIBUTE_STATEMENT_IN_ASSERTION, assertionId);
    }

}

package uk.gov.ida.saml.deserializers.validators;

import uk.gov.ida.saml.core.validation.SamlTransformationErrorException;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;
import uk.gov.ida.saml.errors.SamlTransformationErrorFactory;

public class NotNullSamlStringValidator {
    public void validate(String input) {
        if (input == null) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.noSamlMessage();
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }
    }
}

package uk.gov.ida.saml.hub.validators;

import uk.gov.ida.saml.core.validation.SamlTransformationErrorException;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;
import uk.gov.ida.saml.hub.errors.SamlTransformationErrorFactory;

public class StringSizeValidator {

    public void validate(String input, int lowerBound, int upperBound) {

        if(input.length() < lowerBound){
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.stringTooSmall(input.length(), lowerBound);
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }

        if(input.length() > upperBound){
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.stringTooLarge(input.length(), upperBound);
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }
    }
}

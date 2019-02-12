package uk.gov.ida.saml.security.exception;

import org.slf4j.event.Level;
import uk.gov.ida.saml.core.validation.SamlTransformationErrorException;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;

public class SamlFailedToDecryptException extends SamlTransformationErrorException {

    public SamlFailedToDecryptException(String errorMessage, Exception cause, Level logLevel) {
        super(errorMessage, cause, logLevel);
    }

    public SamlFailedToDecryptException(String errorMessage, Level logLevel) {
        super(errorMessage, logLevel);
    }

    public SamlFailedToDecryptException(SamlValidationSpecificationFailure failure, Exception cause) {
        super(failure.getErrorMessage(), cause, failure.getLogLevel());
    }
}

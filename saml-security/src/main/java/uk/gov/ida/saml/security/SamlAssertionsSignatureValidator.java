package uk.gov.ida.saml.security;

import org.opensaml.saml.saml2.core.Assertion;
import uk.gov.ida.saml.core.validation.SamlTransformationErrorException;
import uk.gov.ida.saml.core.validation.SamlValidationResponse;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;
import uk.gov.ida.saml.security.validators.ValidatedAssertions;

import javax.xml.namespace.QName;
import java.util.List;

public class SamlAssertionsSignatureValidator {

    private final SamlMessageSignatureValidator samlMessageSignatureValidator;

    public SamlAssertionsSignatureValidator(SamlMessageSignatureValidator samlMessageSignatureValidator) {
        this.samlMessageSignatureValidator = samlMessageSignatureValidator;
    }

    public ValidatedAssertions validate(List<Assertion> assertions, QName role) {
        for (Assertion assertion : assertions) {
            final SamlValidationResponse samlValidationResponse = samlMessageSignatureValidator.validate(assertion, role);
            if(!samlValidationResponse.isOK()) {
                SamlValidationSpecificationFailure failure = samlValidationResponse.getSamlValidationSpecificationFailure();
                if (samlValidationResponse.getCause() != null)
                    throw new SamlTransformationErrorException(failure.getErrorMessage(), samlValidationResponse.getCause(), failure.getLogLevel());
                throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
            }
        }
        return new ValidatedAssertions(assertions);
    }

}

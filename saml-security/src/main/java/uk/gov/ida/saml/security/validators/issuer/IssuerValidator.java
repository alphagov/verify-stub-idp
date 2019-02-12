package uk.gov.ida.saml.security.validators.issuer;

import com.google.common.base.Strings;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameIDType;
import uk.gov.ida.saml.core.validation.SamlTransformationErrorException;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;
import uk.gov.ida.saml.security.errors.SamlTransformationErrorFactory;

public class IssuerValidator {
    public void validate(Issuer assertionIssuer) {
        if (assertionIssuer == null) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.missingIssuer();
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }

        if (Strings.isNullOrEmpty(assertionIssuer.getValue())) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.emptyIssuer();
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }

        if (assertionIssuer.getFormat() != null && !NameIDType.ENTITY.equals(assertionIssuer.getFormat())) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.illegalIssuerFormat(assertionIssuer.getFormat(), NameIDType.ENTITY);
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }
    }
}

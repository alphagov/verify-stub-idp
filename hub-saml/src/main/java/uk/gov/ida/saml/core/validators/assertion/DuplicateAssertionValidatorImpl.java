package uk.gov.ida.saml.core.validators.assertion;

import com.google.inject.Inject;
import org.joda.time.DateTime;
import org.opensaml.saml.saml2.core.Assertion;
import uk.gov.ida.saml.hub.exception.SamlValidationException;
import uk.gov.ida.saml.hub.validators.authnrequest.IdExpirationCache;

import static uk.gov.ida.saml.core.errors.SamlTransformationErrorFactory.authnStatementAlreadyReceived;
import static uk.gov.ida.saml.core.errors.SamlTransformationErrorFactory.duplicateMatchingDataset;

public class DuplicateAssertionValidatorImpl implements DuplicateAssertionValidator {

    private final IdExpirationCache<String> idExpirationCache;

    @Inject
    public DuplicateAssertionValidatorImpl(IdExpirationCache<String> idExpirationCache) {
        this.idExpirationCache = idExpirationCache;
    }

    @Override
    public void validateAuthnStatementAssertion(Assertion assertion) {
        if (!valid(assertion))
            throw new SamlValidationException(authnStatementAlreadyReceived(assertion.getID()));
    }

    @Override
    public void validateMatchingDataSetAssertion(Assertion assertion, String responseIssuerId) {
        if (!valid(assertion))
            throw new SamlValidationException(duplicateMatchingDataset(assertion.getID(), responseIssuerId));
    }

    private boolean valid(Assertion assertion) {
        if (isDuplicateNonExpired(assertion))
            return false;

        DateTime expire = assertion.getSubject().getSubjectConfirmations().get(0).getSubjectConfirmationData().getNotOnOrAfter();
        idExpirationCache.setExpiration(assertion.getID(), expire);
        return true;
    }

    private boolean isDuplicateNonExpired(Assertion assertion) {
        return idExpirationCache.contains(assertion.getID())
                && idExpirationCache.getExpiration(assertion.getID()).isAfterNow();
    }
}

package uk.gov.ida.saml.core.transformers;

import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import uk.gov.ida.saml.core.domain.AssertionRestrictions;
import uk.gov.ida.saml.core.domain.IdentityProviderAssertion;
import uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement;
import uk.gov.ida.saml.core.domain.MatchingDataset;
import uk.gov.ida.saml.core.domain.PersistentId;

import java.util.Optional;

public class IdentityProviderAssertionUnmarshaller {
    private final VerifyMatchingDatasetUnmarshaller verifyMatchingDatasetUnmarshaller;
    private final EidasMatchingDatasetUnmarshaller eidasMatchingDatasetUnmarshaller;
    private final IdentityProviderAuthnStatementUnmarshaller identityProviderAuthnStatementUnmarshaller;
    private final String hubEntityId;

    public IdentityProviderAssertionUnmarshaller(
            VerifyMatchingDatasetUnmarshaller verifyMatchingDatasetUnmarshaller,
            EidasMatchingDatasetUnmarshaller eidasMatchingDatasetUnmarshaller,
            IdentityProviderAuthnStatementUnmarshaller identityProviderAuthnStatementUnmarshaller,
            String hubEntityId) {
        this.verifyMatchingDatasetUnmarshaller = verifyMatchingDatasetUnmarshaller;
        this.eidasMatchingDatasetUnmarshaller = eidasMatchingDatasetUnmarshaller;
        this.identityProviderAuthnStatementUnmarshaller = identityProviderAuthnStatementUnmarshaller;
        this.hubEntityId = hubEntityId;
    }

    public IdentityProviderAssertion fromVerifyAssertion(Assertion assertion) {
        MatchingDataset matchingDataset = null;
        IdentityProviderAuthnStatement authnStatement = null;
        if (assertionContainsMatchingDataset(assertion) && !containsAuthnStatement(assertion)) {
            matchingDataset = this.verifyMatchingDatasetUnmarshaller.fromAssertion(assertion);
        } else if (containsAuthnStatement(assertion) && isNotCycle3AssertionFromHub(assertion)) {
            authnStatement = this.identityProviderAuthnStatementUnmarshaller.fromAssertion(assertion);
        }

        return getIdentityProviderAssertion(assertion, matchingDataset, authnStatement);
    }

    @Deprecated
    /**
     * Use {@link this#fromVerifyAssertion} instead
     */
    public IdentityProviderAssertion fromAssertion(Assertion assertion) {
        return fromVerifyAssertion(assertion);
    }

    public IdentityProviderAssertion fromCountryAssertion(Assertion assertion) {
        MatchingDataset matchingDataset = this.eidasMatchingDatasetUnmarshaller.fromAssertion(assertion);
        IdentityProviderAuthnStatement authnStatement = this.identityProviderAuthnStatementUnmarshaller.fromAssertion(assertion);
        return getIdentityProviderAssertion(assertion, matchingDataset, authnStatement);
    }

    private IdentityProviderAssertion getIdentityProviderAssertion(Assertion assertion, MatchingDataset matchingDataset, IdentityProviderAuthnStatement authnStatement) {
        final SubjectConfirmationData subjectConfirmationData = assertion.getSubject().getSubjectConfirmations().get(0).getSubjectConfirmationData();

        AssertionRestrictions assertionRestrictions = new AssertionRestrictions(
                subjectConfirmationData.getNotOnOrAfter(),
                subjectConfirmationData.getInResponseTo(),
                subjectConfirmationData.getRecipient());

        PersistentId persistentId = new PersistentId(assertion.getSubject().getNameID().getValue());
        return new IdentityProviderAssertion(
                assertion.getID(),
                assertion.getIssuer().getValue(),
                assertion.getIssueInstant(),
                persistentId,
                assertionRestrictions,
                Optional.ofNullable(matchingDataset),
                Optional.ofNullable(authnStatement));
    }

    private boolean assertionContainsMatchingDataset(Assertion assertion) {
        // This assumes that the MDS and AuthnStatement are NOT in the same assertion
        return doesAssertionContainAttributes(assertion) && isNotCycle3AssertionFromHub(assertion);
    }

    private boolean containsAuthnStatement(Assertion assertion) {
        return !assertion.getAuthnStatements().isEmpty();
    }

    private boolean doesAssertionContainAttributes(Assertion assertion) {
        return !assertion.getAttributeStatements().isEmpty();
    }

    private boolean isNotCycle3AssertionFromHub(Assertion assertion) {
        return !assertion.getIssuer().getValue().equals(hubEntityId);
    }
}

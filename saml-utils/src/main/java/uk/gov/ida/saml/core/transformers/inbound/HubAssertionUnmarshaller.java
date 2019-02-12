package uk.gov.ida.saml.core.transformers.inbound;

import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import uk.gov.ida.saml.core.domain.AssertionRestrictions;
import uk.gov.ida.saml.core.domain.Cycle3Dataset;
import uk.gov.ida.saml.core.domain.HubAssertion;
import uk.gov.ida.saml.core.domain.PersistentId;

import java.text.MessageFormat;
import java.util.Optional;

public class HubAssertionUnmarshaller {
    private final Cycle3DatasetFactory cycle3DatasetFactory;
    private final String hubEntityId;

    public HubAssertionUnmarshaller(
            Cycle3DatasetFactory cycle3DatasetFactory,
            String hubEntityId) {
        this.cycle3DatasetFactory = cycle3DatasetFactory;
        this.hubEntityId = hubEntityId;
    }

    public HubAssertion toHubAssertion(Assertion assertion) {

        Cycle3Dataset cycle3Dataset;

        if (isCycle3AssertionFromHub(assertion)) {
            cycle3Dataset = cycle3DatasetFactory.createCycle3DataSet(assertion);

            final SubjectConfirmationData subjectConfirmationData = assertion.getSubject().getSubjectConfirmations().get(0).getSubjectConfirmationData();

            AssertionRestrictions assertionRestrictions = new AssertionRestrictions(
                    subjectConfirmationData.getNotOnOrAfter(),
                    subjectConfirmationData.getInResponseTo(),
                    subjectConfirmationData.getRecipient());

            PersistentId persistentId = new PersistentId(assertion.getSubject().getNameID().getValue());

            return new HubAssertion(
                    assertion.getID(),
                    assertion.getIssuer().getValue(),
                    assertion.getIssueInstant(),
                    persistentId,
                    assertionRestrictions,
                    Optional.ofNullable(cycle3Dataset)
            );
        }

        throw new IllegalStateException(MessageFormat.format("{0} - This assertion does not contain cycle 3 data.", assertion.getID()));
    }

    private boolean isCycle3AssertionFromHub(Assertion assertion) {
        return assertion.getIssuer().getValue().equals(hubEntityId);
    }
}

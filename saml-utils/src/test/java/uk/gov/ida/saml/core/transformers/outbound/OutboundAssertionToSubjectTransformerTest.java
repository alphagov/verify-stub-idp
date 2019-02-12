package uk.gov.ida.saml.core.transformers.outbound;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.domain.AssertionRestrictions;
import uk.gov.ida.saml.core.domain.HubAssertion;
import uk.gov.ida.saml.core.domain.PersistentId;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;
import uk.gov.ida.saml.core.test.builders.PersistentIdBuilder;

import static uk.gov.ida.saml.core.test.builders.HubAssertionBuilder.aHubAssertion;

@RunWith(OpenSAMLRunner.class)
public class OutboundAssertionToSubjectTransformerTest {

    private OutboundAssertionToSubjectTransformer transformer;

    @Before
    public void setup() {
        transformer = new OutboundAssertionToSubjectTransformer(new OpenSamlXmlObjectFactory());
    }


    @Test
    public void transform_shouldAddSubjectConfirmationData() throws Exception {
        HubAssertion assertion = aHubAssertion().build();

        final Subject subject = transformer.transform(assertion);

        final SubjectConfirmation subjectConfirmation = subject.getSubjectConfirmations().get(0);
        Assertions.assertThat(subjectConfirmation.getMethod()).isEqualTo(SubjectConfirmation.METHOD_BEARER);
        final AssertionRestrictions assertionRestrictions = assertion.getAssertionRestrictions();
        Assertions.assertThat(subjectConfirmation.getSubjectConfirmationData().getRecipient()).isEqualTo(assertionRestrictions.getRecipient());
        org.assertj.jodatime.api.Assertions.assertThat(subjectConfirmation.getSubjectConfirmationData().getNotOnOrAfter()).isEqualTo(assertionRestrictions.getNotOnOrAfter());
        Assertions.assertThat(subjectConfirmation.getSubjectConfirmationData().getInResponseTo()).isEqualTo(assertionRestrictions.getInResponseTo());
    }

    @Test
    public void transform_shouldTransformPersistentId() throws Exception {
        PersistentId persistentId = PersistentIdBuilder.aPersistentId().build();
        HubAssertion assertion = aHubAssertion().withPersistentId(persistentId).build();

        Subject subject = transformer.transform(assertion);

        NameID nameID = subject.getNameID();
        Assertions.assertThat(nameID.getValue()).isEqualTo(persistentId.getNameId());
        Assertions.assertThat(nameID.getFormat()).isEqualTo(NameIDType.PERSISTENT);

    }
}

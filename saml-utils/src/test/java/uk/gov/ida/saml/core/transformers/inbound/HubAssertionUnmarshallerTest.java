package uk.gov.ida.saml.core.transformers.inbound;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import uk.gov.ida.saml.core.domain.AssertionRestrictions;
import uk.gov.ida.saml.core.domain.Cycle3Dataset;
import uk.gov.ida.saml.core.domain.HubAssertion;
import uk.gov.ida.saml.core.test.OpenSAMLMockitoRunner;
import uk.gov.ida.saml.core.test.builders.Cycle3DatasetBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.core.test.TestEntityIds.HUB_ENTITY_ID;
import static uk.gov.ida.saml.core.test.builders.AssertionBuilder.aCycle3DatasetAssertion;
import static uk.gov.ida.saml.core.test.builders.AssertionBuilder.anAssertion;
import static uk.gov.ida.saml.core.test.builders.AttributeStatementBuilder.anAttributeStatement;
import static uk.gov.ida.saml.core.test.builders.IssuerBuilder.anIssuer;

@RunWith(OpenSAMLMockitoRunner.class)
public class HubAssertionUnmarshallerTest {

    @Mock
    private Cycle3DatasetFactory assertionCycle3DatasetTransformer;

    private HubAssertionUnmarshaller hubAssertionUnmarshaller;

    @Before
    public void setUp() throws Exception {
        hubAssertionUnmarshaller = new HubAssertionUnmarshaller(
                assertionCycle3DatasetTransformer, HUB_ENTITY_ID);
    }

    @Test
    public void transform_shouldDelegateCycle3DataTransformation() throws Exception {
        Assertion cycle3Assertion = aCycle3DatasetAssertion("name", "value").buildUnencrypted();
        Cycle3Dataset cycle3Data = Cycle3DatasetBuilder.aCycle3Dataset().addCycle3Data("name", "value").build();
        Mockito.when(assertionCycle3DatasetTransformer.createCycle3DataSet(cycle3Assertion)).thenReturn(cycle3Data);

        HubAssertion hubAssertion = hubAssertionUnmarshaller.toHubAssertion(cycle3Assertion);

        assertThat(hubAssertion.getCycle3Data().isPresent()).isEqualTo(true);
        assertThat(hubAssertion.getCycle3Data().get()).isEqualTo(cycle3Data);
    }

    @Test
    public void transform_shouldTransformSubjectConfirmationData() throws Exception {
        Assertion assertion = anAssertion()
                .withIssuer(anIssuer().withIssuerId(HUB_ENTITY_ID).build())
                .addAttributeStatement(anAttributeStatement().build()).buildUnencrypted();
        SubjectConfirmationData subjectConfirmationData = assertion.getSubject().getSubjectConfirmations().get(0).getSubjectConfirmationData();

        final HubAssertion hubAssertion = hubAssertionUnmarshaller.toHubAssertion(assertion);

        final AssertionRestrictions assertionRestrictions = hubAssertion.getAssertionRestrictions();

        assertThat(assertionRestrictions.getInResponseTo()).isEqualTo(subjectConfirmationData.getInResponseTo());
        assertThat(assertionRestrictions.getRecipient()).isEqualTo(subjectConfirmationData.getRecipient());
        assertThat(assertionRestrictions.getNotOnOrAfter()).isEqualTo(subjectConfirmationData.getNotOnOrAfter());
    }
}

package uk.gov.ida.saml.core.transformers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import uk.gov.ida.saml.core.domain.AssertionRestrictions;
import uk.gov.ida.saml.core.domain.IdentityProviderAssertion;
import uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement;
import uk.gov.ida.saml.core.domain.MatchingDataset;
import uk.gov.ida.saml.core.test.OpenSAMLMockitoRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ida.saml.core.test.builders.AddressAttributeBuilder_1_1.anAddressAttribute;
import static uk.gov.ida.saml.core.test.builders.AddressAttributeValueBuilder_1_1.anAddressAttributeValue;
import static uk.gov.ida.saml.core.test.builders.AssertionBuilder.aMatchingDatasetAssertion;
import static uk.gov.ida.saml.core.test.builders.AssertionBuilder.anAssertion;
import static uk.gov.ida.saml.core.test.builders.AssertionBuilder.anAuthnStatementAssertion;
import static uk.gov.ida.saml.core.test.builders.DateAttributeBuilder_1_1.aDate_1_1;
import static uk.gov.ida.saml.core.test.builders.GenderAttributeBuilder_1_1.aGender_1_1;
import static uk.gov.ida.saml.core.test.builders.IdentityProviderAuthnStatementBuilder.anIdentityProviderAuthnStatement;
import static uk.gov.ida.saml.core.test.builders.MatchingDatasetBuilder.aMatchingDataset;
import static uk.gov.ida.saml.core.test.builders.PersonNameAttributeBuilder_1_1.aPersonName_1_1;
import static uk.gov.ida.saml.core.test.builders.PersonNameAttributeValueBuilder.aPersonNameValue;

@RunWith(OpenSAMLMockitoRunner.class)
public class IdentityProviderAssertionUnmarshallerTest {

    @Mock
    private VerifyMatchingDatasetUnmarshaller matchingDatasetUnmarshaller;

    @Mock
    private IdentityProviderAuthnStatementUnmarshaller idaAuthnStatementUnmarshaller;

    @Mock
    private EidasMatchingDatasetUnmarshaller eidasMatchingDatasetUnmarshaller;

    private IdentityProviderAssertionUnmarshaller unmarshaller;

    @Before
    public void setUp() throws Exception {
        unmarshaller = new IdentityProviderAssertionUnmarshaller(
                matchingDatasetUnmarshaller,
                eidasMatchingDatasetUnmarshaller,
                idaAuthnStatementUnmarshaller,
                "hubEntityId");
    }

    @Test
    public void transform_shouldTransformResponseWhenNoMatchingDatasetIsPresent() throws Exception {
        Assertion originalAssertion = anAssertion().buildUnencrypted();

        IdentityProviderAssertion transformedAssertion = unmarshaller.fromVerifyAssertion(originalAssertion);
        assertThat(transformedAssertion.getMatchingDataset()).isEqualTo(Optional.empty());
    }

    @Test
    public void transform_shouldDelegateMatchingDatasetTransformationWhenAssertionContainsMatchingDataset() throws Exception {
        Attribute firstName = aPersonName_1_1().addValue(aPersonNameValue().withTo(DateTime.parse("1066-01-05")).build()).buildAsFirstname();
        Assertion assertion = aMatchingDatasetAssertion(
                firstName,
                aPersonName_1_1().buildAsMiddlename(),
                aPersonName_1_1().buildAsSurname(),
                aGender_1_1().build(),
                aDate_1_1().buildAsDateOfBirth(),
                anAddressAttribute().buildCurrentAddress(),
                anAddressAttribute().addAddress(anAddressAttributeValue().build()).buildPreviousAddress())
                .buildUnencrypted();

        MatchingDataset matchingDataset = aMatchingDataset().build();

        when(matchingDatasetUnmarshaller.fromAssertion(assertion)).thenReturn(matchingDataset);

        IdentityProviderAssertion identityProviderAssertion = unmarshaller.fromVerifyAssertion(assertion);
        verify(matchingDatasetUnmarshaller).fromAssertion(assertion);
        assertThat(identityProviderAssertion.getMatchingDataset().get()).isEqualTo(matchingDataset);
    }

    @Test
    public void transform_shouldDelegateAuthnStatementTransformationWhenAssertionContainsAuthnStatement() throws Exception {
        Assertion assertion = anAuthnStatementAssertion().buildUnencrypted();
        IdentityProviderAuthnStatement authnStatement = anIdentityProviderAuthnStatement().build();

        when(idaAuthnStatementUnmarshaller.fromAssertion(assertion)).thenReturn(authnStatement);
        IdentityProviderAssertion identityProviderAssertion = unmarshaller.fromVerifyAssertion(assertion);

        verify(idaAuthnStatementUnmarshaller).fromAssertion(assertion);

        assertThat(identityProviderAssertion.getAuthnStatement().get()).isEqualTo(authnStatement);
    }

    @Test
    public void transform_shouldTransformSubjectConfirmationData() throws Exception {
        Assertion assertion = anAssertion().buildUnencrypted();
        SubjectConfirmationData subjectConfirmationData = assertion.getSubject().getSubjectConfirmations().get(0).getSubjectConfirmationData();

        final IdentityProviderAssertion identityProviderAssertion = unmarshaller.fromVerifyAssertion(assertion);

        final AssertionRestrictions assertionRestrictions = identityProviderAssertion.getAssertionRestrictions();

        assertThat(assertionRestrictions.getInResponseTo()).isEqualTo(subjectConfirmationData.getInResponseTo());
        assertThat(assertionRestrictions.getRecipient()).isEqualTo(subjectConfirmationData.getRecipient());
        assertThat(assertionRestrictions.getNotOnOrAfter()).isEqualTo(subjectConfirmationData.getNotOnOrAfter());
    }
}

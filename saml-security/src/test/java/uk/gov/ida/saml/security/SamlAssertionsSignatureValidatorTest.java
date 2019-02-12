package uk.gov.ida.saml.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import uk.gov.ida.saml.core.test.TestEntityIds;
import uk.gov.ida.saml.core.validation.SamlTransformationErrorException;
import uk.gov.ida.saml.core.validation.SamlValidationResponse;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;
import uk.gov.ida.saml.security.saml.OpenSAMLMockitoRunner;
import uk.gov.ida.saml.security.saml.builders.AssertionBuilder;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ida.saml.security.errors.SamlTransformationErrorFactory.invalidSignatureForAssertion;
import static uk.gov.ida.saml.security.saml.builders.IssuerBuilder.anIssuer;

@RunWith(OpenSAMLMockitoRunner.class)
public class SamlAssertionsSignatureValidatorTest {

    private final String issuerId = TestEntityIds.HUB_ENTITY_ID;
    private final SigningCredentialFactory credentialFactory = new SigningCredentialFactory(new HardCodedKeyStore(issuerId));
    private final CredentialFactorySignatureValidator signatureValidator = new CredentialFactorySignatureValidator(credentialFactory);

    private SamlMessageSignatureValidator samlMessageSignatureValidator;
    private SamlAssertionsSignatureValidator samlAssertionsSignatureValidator;

    @Before
    public void initSpy() {
        samlMessageSignatureValidator = spy(new SamlMessageSignatureValidator(signatureValidator));
        samlAssertionsSignatureValidator = new SamlAssertionsSignatureValidator(samlMessageSignatureValidator);
    }

    @Test
    public void shouldValidateAllAssertions() {
        final Assertion assertion1 = AssertionBuilder.anAuthnStatementAssertion();
        final Assertion assertion2 = AssertionBuilder.anAssertion().build();
        final List<Assertion> assertions = asList(assertion1, assertion2);

        samlAssertionsSignatureValidator.validate(assertions, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);

        verify(samlMessageSignatureValidator).validate(assertion1, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        verify(samlMessageSignatureValidator).validate(assertion2, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
    }

    @Test(expected = SamlTransformationErrorException.class)
    public void shouldFailOnFirstBadlySignedAssertion() {
        final Assertion assertion1 = AssertionBuilder.anAssertion().withoutSigning().build();
        final Assertion assertion2 = AssertionBuilder.anAuthnStatementAssertion();
        final List<Assertion> assertions = asList(assertion1, assertion2);

        try {
            samlAssertionsSignatureValidator.validate(assertions, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        } catch(SamlTransformationErrorException e) {
            verify(samlMessageSignatureValidator).validate(assertion1, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
            verify(samlMessageSignatureValidator, never()).validate(assertion2, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
            throw e;
        }

        fail("Should have failed on badly signed assertion.");
    }

    @Test
    public void shouldFailOnAssertionSignedWithWrongIssuer() throws Exception {
        try {
            final Assertion assertion = AssertionBuilder.anAuthnStatementAssertion();
            when(samlMessageSignatureValidator.validate(assertion, IDPSSODescriptor.DEFAULT_ELEMENT_NAME)).thenReturn(SamlValidationResponse.aValidResponse());

            final Assertion badAssertion = AssertionBuilder
                    .anAssertion()
                    .withIssuer(anIssuer().withIssuerId(TestEntityIds.HUB_ENTITY_ID).build())
                    .build();

            final SamlValidationSpecificationFailure samlValidationSpecificationFailure = invalidSignatureForAssertion("ID");

            when(samlMessageSignatureValidator.validate(badAssertion, IDPSSODescriptor.DEFAULT_ELEMENT_NAME)).thenReturn(SamlValidationResponse.anInvalidResponse(samlValidationSpecificationFailure));
            samlAssertionsSignatureValidator.validate(asList(assertion, badAssertion), IDPSSODescriptor.DEFAULT_ELEMENT_NAME);

            fail("expected exception");
        } catch (SamlTransformationErrorException e) {
            final String expected = "SAML Validation Specification: Signature for assertion ID was not valid.\n" +
                    "DocumentReference{documentName='Hub Service Profile 1.1a', documentSection=''}";
            assertThat(e.getMessage()).isEqualTo(expected);
        }
    }
}

package uk.gov.ida.saml.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AttributeQuery;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.security.credential.Credential;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.core.test.TestEntityIds;
import uk.gov.ida.saml.core.validation.SamlValidationResponse;
import uk.gov.ida.saml.security.errors.SamlTransformationErrorFactory;
import uk.gov.ida.saml.security.saml.OpenSAMLMockitoRunner;
import uk.gov.ida.saml.security.saml.TestCredentialFactory;
import uk.gov.ida.saml.security.saml.builders.AttributeQueryBuilder;
import uk.gov.ida.saml.security.saml.builders.ResponseBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.security.saml.builders.AssertionBuilder.anAssertion;
import static uk.gov.ida.saml.security.saml.builders.AuthnRequestBuilder.anAuthnRequest;
import static uk.gov.ida.saml.security.saml.builders.IssuerBuilder.anIssuer;

@RunWith(OpenSAMLMockitoRunner.class)
public class SamlMessageSignatureValidatorTest {

    private final String issuerId = TestEntityIds.HUB_ENTITY_ID;
    private final SigningCredentialFactory credentialFactory = new SigningCredentialFactory(new HardCodedKeyStore(issuerId));
    private final CredentialFactorySignatureValidator signatureValidator = new CredentialFactorySignatureValidator(credentialFactory);
    private final SamlMessageSignatureValidator samlMessageSignatureValidator = new SamlMessageSignatureValidator(signatureValidator);

    @Test
    public void validateWithIssue_shouldReturnBadResponseIfRequestSignatureIsMissing() {
        final AuthnRequest unsignedAuthnRequest = anAuthnRequest()
                .withIssuer(anIssuer().withIssuerId(issuerId).build())
                .withoutSignatureElement()
                .build();

        SamlValidationResponse signatureValidationResponse = samlMessageSignatureValidator.validate(unsignedAuthnRequest, SPSSODescriptor.DEFAULT_ELEMENT_NAME);

        assertThat(signatureValidationResponse.isOK()).isFalse();
        assertThat(signatureValidationResponse.getErrorMessage()).isEqualTo(SamlTransformationErrorFactory.missingSignature().getErrorMessage());
    }

    @Test
    public void validateWithIssue_shouldReturnBadResponseIfRequestIsNotSigned() {
        final AuthnRequest unsignedAuthnRequest = anAuthnRequest()
                .withIssuer(anIssuer().withIssuerId(issuerId).build())
                .withoutSigning()
                .build();

        SamlValidationResponse signatureValidationResponse = samlMessageSignatureValidator.validate(unsignedAuthnRequest, SPSSODescriptor.DEFAULT_ELEMENT_NAME);

        assertThat(signatureValidationResponse.isOK()).isFalse();
        assertThat(signatureValidationResponse.getErrorMessage()).isEqualTo(SamlTransformationErrorFactory.signatureNotSigned().getErrorMessage());
    }

    @Test
    public void validateWithIssue_shouldReturnBadResponseIfRequestSignatureIsBad() {
        Credential badCredential = new TestCredentialFactory(TestCertificateStrings.UNCHAINED_PUBLIC_CERT, TestCertificateStrings.UNCHAINED_PRIVATE_KEY).getSigningCredential();
        final AuthnRequest unsignedAuthnRequest = anAuthnRequest()
                .withIssuer(anIssuer().withIssuerId(issuerId).build())
                .withSigningCredential(badCredential)
                .build();

        SamlValidationResponse signatureValidationResponse = samlMessageSignatureValidator.validate(unsignedAuthnRequest, SPSSODescriptor.DEFAULT_ELEMENT_NAME);

        assertThat(signatureValidationResponse.isOK()).isFalse();
        assertThat(signatureValidationResponse.getErrorMessage()).isEqualTo(SamlTransformationErrorFactory.invalidMessageSignature().getErrorMessage());
    }

    @Test
    public void validate_shouldAcceptSignedAuthnRequest() {
        final AuthnRequest signedAuthnRequest = anAuthnRequest().build();

        SamlValidationResponse signatureValidationResponse = samlMessageSignatureValidator.validate(signedAuthnRequest, SPSSODescriptor.DEFAULT_ELEMENT_NAME);

        assertThat(signatureValidationResponse.isOK()).isTrue();
    }

    @Test
    public void validate_shouldAcceptSignedAssertion() {
        final Assertion signedAssertion = anAssertion().build();

        SamlValidationResponse signatureValidationResponse = samlMessageSignatureValidator.validate(signedAssertion, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);

        assertThat(signatureValidationResponse.isOK()).isTrue();
    }

    @Test
    public void validate_shouldAcceptSignedAttributeQuery() {
        final AttributeQuery signedAttributeQuery = AttributeQueryBuilder.anAttributeQuery().build();

        SamlValidationResponse signatureValidationResponse = samlMessageSignatureValidator.validate(signedAttributeQuery, SPSSODescriptor.DEFAULT_ELEMENT_NAME);

        assertThat(signatureValidationResponse.isOK()).isTrue();
    }

    @Test
    public void validate_shouldAcceptSignedResponse() throws Exception {
        final Response signedResponse = ResponseBuilder.aResponse().build();

        SamlValidationResponse signatureValidationResponse = samlMessageSignatureValidator.validate(signedResponse, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);

        assertThat(signatureValidationResponse.isOK()).isTrue();
    }

    @Test
    public void validate_shouldReturnBadResponseIfIssuerIsMissing() {
        final AuthnRequest signedAuthnRequest = anAuthnRequest()
                .withIssuer(null)
                .build();

        SamlValidationResponse signatureValidationResponse = samlMessageSignatureValidator.validate(signedAuthnRequest, SPSSODescriptor.DEFAULT_ELEMENT_NAME);

        assertThat(signatureValidationResponse.isOK()).isFalse();
        assertThat(signatureValidationResponse.getErrorMessage()).isEqualTo(SamlTransformationErrorFactory.missingIssuer().getErrorMessage());
    }

    @Test
    public void validate_shouldReturnBadResponseIfIssuerIsEmpty() {
        final AuthnRequest signedAuthnRequest = anAuthnRequest()
                .withIssuer(anIssuer().withIssuerId("").build())
                .build();

        SamlValidationResponse signatureValidationResponse = samlMessageSignatureValidator.validate(signedAuthnRequest, SPSSODescriptor.DEFAULT_ELEMENT_NAME);

        assertThat(signatureValidationResponse.isOK()).isFalse();
        assertThat(signatureValidationResponse.getErrorMessage()).isEqualTo(SamlTransformationErrorFactory.emptyIssuer().getErrorMessage());
    }
}

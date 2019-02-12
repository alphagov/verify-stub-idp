package uk.gov.ida.saml.security;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.signature.support.SignatureException;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.core.test.TestEntityIds;
import uk.gov.ida.saml.security.saml.OpenSAMLMockitoRunner;
import uk.gov.ida.saml.security.saml.StringEncoding;
import uk.gov.ida.saml.security.saml.TestCredentialFactory;
import uk.gov.ida.saml.security.saml.builders.AssertionBuilder;
import uk.gov.ida.saml.security.saml.builders.SignatureBuilder;
import uk.gov.ida.saml.security.saml.deserializers.AuthnRequestUnmarshaller;
import uk.gov.ida.saml.security.saml.deserializers.SamlObjectParser;
import uk.gov.ida.saml.security.saml.deserializers.StringToOpenSamlObjectTransformer;

import java.net.URL;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(OpenSAMLMockitoRunner.class)
public class CredentialFactorySignatureValidatorTest {
    private final String issuerId = TestEntityIds.HUB_ENTITY_ID;
    private final SigningCredentialFactory credentialFactory = new SigningCredentialFactory(new HardCodedKeyStore(issuerId));
    private final CredentialFactorySignatureValidator credentialFactorySignatureValidator = new CredentialFactorySignatureValidator(credentialFactory);

    @Test
    public void shouldAcceptSignedAssertions() throws Exception {
        Credential signingCredential = new TestCredentialFactory(TestCertificateStrings.HUB_TEST_PUBLIC_SIGNING_CERT, TestCertificateStrings.HUB_TEST_PRIVATE_SIGNING_KEY).getSigningCredential();
        final Assertion assertion = AssertionBuilder.anAssertion().withSignature(SignatureBuilder.aSignature().withSigningCredential(signingCredential).build()).build();
        assertThat(credentialFactorySignatureValidator.validate(assertion, issuerId, null)).isEqualTo(true);
    }

    @Test
    public void shouldNotAcceptUnsignedAssertions() throws Exception {
        assertThat(credentialFactorySignatureValidator.validate(AssertionBuilder.anAssertion().withoutSigning().build(), issuerId, null)).isEqualTo(false);
    }

    @Test
    public void shouldNotAcceptMissignedAssertions() throws Exception {
        Credential badSigningCredential = new TestCredentialFactory(TestCertificateStrings.UNCHAINED_PUBLIC_CERT, TestCertificateStrings.UNCHAINED_PRIVATE_KEY).getSigningCredential();
        final Assertion assertion = AssertionBuilder.anAssertion().withSignature(SignatureBuilder.aSignature().withSigningCredential(badSigningCredential).build()).build();
        assertThat(credentialFactorySignatureValidator.validate(assertion, issuerId, null)).isEqualTo(false);
    }

    @Test
    public void shouldSupportAnEntityWithMultipleSigningCertificates() throws Exception {
        List<String> certificates = asList(TestCertificateStrings.HUB_TEST_PUBLIC_SIGNING_CERT, TestCertificateStrings.HUB_TEST_SECONDARY_PUBLIC_SIGNING_CERT);
        final ImmutableMap<String, List<String>> publicKeys = ImmutableMap.of(issuerId, certificates);
        final InjectableSigningKeyStore injectableSigningKeyStore = new InjectableSigningKeyStore(publicKeys);
        final CredentialFactorySignatureValidator credentialFactorySignatureValidator = new CredentialFactorySignatureValidator(new SigningCredentialFactory(injectableSigningKeyStore));

        Credential firstSigningCredential = new TestCredentialFactory(TestCertificateStrings.HUB_TEST_PUBLIC_SIGNING_CERT, TestCertificateStrings.HUB_TEST_PRIVATE_SIGNING_KEY).getSigningCredential();
        boolean validate = credentialFactorySignatureValidator.validate(AssertionBuilder.anAssertion().withSignature(SignatureBuilder.aSignature().withSigningCredential(firstSigningCredential).build()).build(), issuerId, null);
        assertThat(validate).isEqualTo(true);

        Credential secondSigningCredential = new TestCredentialFactory(TestCertificateStrings.HUB_TEST_SECONDARY_PUBLIC_SIGNING_CERT, TestCertificateStrings.HUB_TEST_PRIVATE_SECONDARY_SIGNING_KEY).getSigningCredential();
        validate = credentialFactorySignatureValidator.validate(AssertionBuilder.anAssertion().withSignature(SignatureBuilder.aSignature().withSigningCredential(secondSigningCredential).build()).build(), issuerId, null);
        assertThat(validate).isEqualTo(true);

        Credential thirdSigningCredential = new TestCredentialFactory(TestCertificateStrings.UNCHAINED_PUBLIC_CERT, TestCertificateStrings.UNCHAINED_PRIVATE_KEY).getSigningCredential();
        validate = credentialFactorySignatureValidator.validate(AssertionBuilder.anAssertion().withSignature(SignatureBuilder.aSignature().withSigningCredential(thirdSigningCredential).build()).build(), issuerId, null);
        assertThat(validate).isEqualTo(false);
    }

    /*
     * Signature algorithm should be valid.
     */
    @Test
    public void shouldNotValidateBadSignatureAlgorithm() throws Exception {
        URL authnRequestUrl = getClass().getClassLoader().getResource("authnRequestBadAlgorithm.xml");
        String input = StringEncoding.toBase64Encoded(Resources.toString(authnRequestUrl, Charsets.UTF_8));
        AuthnRequest request = getStringtoOpenSamlObjectTransformer().apply(input);
        assertThat(credentialFactorySignatureValidator.validate(request, issuerId, null)).isFalse();
    }

    /*
     * Signature object should exist.
     */
    @Test(expected = SignatureException.class)
    public void shouldNotValidateMissingSignature() throws Exception {
        validateAuthnRequestFile("authnRequestNoSignature.xml");
    }

    /*
     * Signature must be an immediate child of the SAML object.
     */
    @Test(expected = SignatureException.class)
    public void shouldNotValidateSignatureNotImmediateChild() throws Exception {
        validateAuthnRequestFile("authnRequestNotImmediateChild.xml");
    }

    /*
     * Signature should not contain more than one Reference.
     */
    @Test(expected = SignatureException.class)
    public void shouldNotValidateSignatureTooManyReferences() throws Exception {
        validateAuthnRequestFile("authnRequestTooManyRefs.xml");
    }

    /*
     * Reference requires a valid URI pointing to a fragment ID.
     */
    @Test(expected = SignatureException.class)
    public void shouldNotValidateSignatureBadReferenceURI() throws Exception {
        validateAuthnRequestFile("authnRequestBadRefURI.xml");
    }

    /*
     * Reference URI should point to parent SAML object.
     */
    @Test(expected = SignatureException.class)
    public void shouldNotValidateSignatureReferenceURINotParentID() throws Exception {
        validateAuthnRequestFile("authnRequestRefURINotParentID.xml");
    }

    /*
     * Root SAML object should have an ID.
     */
    @Test(expected = SignatureException.class)
    public void shouldNotValidateSignatureNoParentID() throws Exception {
        validateAuthnRequestFile("authnRequestNoParentID.xml");
    }

    /*
     * Signature must have Transforms defined.
     */
    @Test(expected = SignatureException.class)
    public void shouldNotValidateSignatureNoTransforms() throws Exception {
        validateAuthnRequestFile("authnRequestNoTransforms.xml");
    }

    /*
     * Signature should not have more than two Transforms.
     */
    @Test(expected = SignatureException.class)
    public void shouldNotValidateSignatureTooManyTransforms() throws Exception {
        validateAuthnRequestFile("authnRequestTooManyTransforms.xml");
    }

    /*
     * Signature must have enveloped-signature Transform.
     */
    @Test(expected = SignatureException.class)
    public void shouldNotValidateSignatureNoEnvelopeTransform() throws Exception {
        validateAuthnRequestFile("authnRequestNoEnvTransform.xml");
    }

    /*
     * Signature must have a valid enveloped-signature Transform.
     */
    @Test(expected = SignatureException.class)
    public void shouldNotValidateSignatureInvalidEnvelopeTransform() throws Exception {
        validateAuthnRequestFile("authnRequestInvalidEnvTransform.xml");
    }

    /*
     * Signature should not contain any Object children.
     */
    @Test(expected = SignatureException.class)
    public void shouldNotValidateSignatureContainingObject() throws Exception {
        validateAuthnRequestFile("authnRequestSigContainsChildren.xml");
    }

    private void validateAuthnRequestFile(String fileName) throws Exception {
        URL authnRequestUrl = getClass().getClassLoader().getResource(fileName);
        String input = StringEncoding.toBase64Encoded(Resources.toString(authnRequestUrl, Charsets.UTF_8));
        AuthnRequest request = getStringtoOpenSamlObjectTransformer().apply(input);
        credentialFactorySignatureValidator.validate(request, issuerId, null);
    }

    private StringToOpenSamlObjectTransformer getStringtoOpenSamlObjectTransformer() {
        return new StringToOpenSamlObjectTransformer(new AuthnRequestUnmarshaller(new SamlObjectParser()));
    }
}

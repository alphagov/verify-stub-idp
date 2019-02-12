package uk.gov.ida.saml.security;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.signature.support.SignatureException;
import uk.gov.ida.common.shared.security.PrivateKeyFactory;
import uk.gov.ida.common.shared.security.PublicKeyFactory;
import uk.gov.ida.common.shared.security.X509CertificateFactory;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.core.test.TestEntityIds;
import uk.gov.ida.saml.security.exception.SamlFailedToDecryptException;
import uk.gov.ida.saml.security.saml.OpenSAMLMockitoRunner;
import uk.gov.ida.saml.security.saml.TestCredentialFactory;
import uk.gov.ida.saml.security.saml.builders.EncryptedAssertionBuilder;
import uk.gov.ida.saml.security.validators.ValidatedResponse;
import uk.gov.ida.saml.security.validators.encryptedelementtype.EncryptionAlgorithmValidator;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static uk.gov.ida.saml.security.saml.builders.EncryptedAssertionBuilder.anEncryptedAssertionBuilder;
import static uk.gov.ida.saml.security.saml.builders.IssuerBuilder.anIssuer;
import static uk.gov.ida.saml.security.saml.builders.ResponseBuilder.aResponse;

@RunWith(OpenSAMLMockitoRunner.class)
public class AssertionDecrypterTest {

    private final String assertionId = "test-assertion";
    private IdaKeyStoreCredentialRetriever keyStoreCredentialRetriever;
    private AssertionDecrypter assertionDecrypter;
    private PublicKeyFactory publicKeyFactory;

    @Before
    public void setup() throws Exception {
        publicKeyFactory = new PublicKeyFactory(new X509CertificateFactory());
        PrivateKey privateKey = new PrivateKeyFactory().createPrivateKey(Base64.decodeBase64(TestCertificateStrings.PRIVATE_SIGNING_KEYS.get(
                TestEntityIds.HUB_ENTITY_ID)));
        PublicKey publicKey = publicKeyFactory.createPublicKey(TestCertificateStrings.getPrimaryPublicEncryptionCert(TestEntityIds.HUB_ENTITY_ID));

        PrivateKey privateEncryptionKey = new PrivateKeyFactory().createPrivateKey(Base64.decodeBase64(TestCertificateStrings.HUB_TEST_PRIVATE_ENCRYPTION_KEY));
        PublicKey publicEncryptionKey = publicKeyFactory.createPublicKey(TestCertificateStrings.HUB_TEST_PUBLIC_ENCRYPTION_CERT);

        KeyPair encryptionKeyPair = new KeyPair(publicEncryptionKey, privateEncryptionKey);

        keyStoreCredentialRetriever = new IdaKeyStoreCredentialRetriever(
                new IdaKeyStore(new KeyPair(publicKey, privateKey), Arrays.asList(encryptionKeyPair))
        );
        List<Credential> credentials = keyStoreCredentialRetriever.getDecryptingCredentials();
        Decrypter decrypter = new DecrypterFactory().createDecrypter(credentials);
        assertionDecrypter = new AssertionDecrypter(new EncryptionAlgorithmValidator(), decrypter);
    }

    @Test
    public void shouldConvertEncryptedAssertionIntoAssertion() throws Exception {
        final Response response = responseForAssertion(EncryptedAssertionBuilder.anEncryptedAssertionBuilder().withPublicEncryptionCert(TestCertificateStrings.HUB_TEST_PUBLIC_ENCRYPTION_CERT).withId(assertionId).build());
        final List<Assertion> assertions = assertionDecrypter.decryptAssertions(new ValidatedResponse(response));
        assertEquals(assertions.get(0).getID(), assertionId);
    }

    @Test (expected = SamlFailedToDecryptException.class)
    public void throwsExceptionIfCannotDecryptAssertions() throws MarshallingException, SignatureException {
        final EncryptedAssertion badlyEncryptedAssertion = anEncryptedAssertionBuilder().withId(assertionId).withEncrypterCredential(
                new TestCredentialFactory(TestCertificateStrings.STUB_IDP_PUBLIC_PRIMARY_CERT, null).getEncryptingCredential()).build();

        final Response response = responseForAssertion(badlyEncryptedAssertion);

        assertionDecrypter.decryptAssertions(new ValidatedResponse(response));
    }

    private Response responseForAssertion(EncryptedAssertion encryptedAssertion) throws MarshallingException, SignatureException {
        return aResponse()
                .withSigningCredential(keyStoreCredentialRetriever.getSigningCredential())
                .withIssuer(anIssuer().withIssuerId(TestEntityIds.STUB_IDP_ONE).build())
                .addEncryptedAssertion(encryptedAssertion)
                .build();
    }
}

package uk.gov.ida.saml.security;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA256;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA256;
import uk.gov.ida.common.shared.security.PrivateKeyFactory;
import uk.gov.ida.common.shared.security.PublicKeyFactory;
import uk.gov.ida.common.shared.security.X509CertificateFactory;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.core.test.TestEntityIds;
import uk.gov.ida.saml.security.saml.OpenSAMLMockitoRunner;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.security.saml.builders.AssertionBuilder.anAssertion;


@RunWith(OpenSAMLMockitoRunner.class)
public class SignatureWithKeyInfoFactoryTest {

    private PublicKeyFactory publicKeyFactory;

	@Test
    public void shouldCreateMultipleSignaturesWithoutThrowingExceptions() throws Exception {
        final String id = UUID.randomUUID().toString();
        publicKeyFactory = new PublicKeyFactory(new X509CertificateFactory());
        PrivateKey privateKey = new PrivateKeyFactory().createPrivateKey(Base64.decodeBase64(TestCertificateStrings.PRIVATE_SIGNING_KEYS.get(
		        TestEntityIds.HUB_ENTITY_ID)));
        PublicKey publicKey = publicKeyFactory.createPublicKey(TestCertificateStrings.getPrimaryPublicEncryptionCert(TestEntityIds.HUB_ENTITY_ID));

        PrivateKey privateEncryptionKey = new PrivateKeyFactory().createPrivateKey(Base64.decodeBase64(TestCertificateStrings.HUB_TEST_PRIVATE_ENCRYPTION_KEY));
        PublicKey publicEncryptionKey = publicKeyFactory.createPublicKey(TestCertificateStrings.HUB_TEST_PUBLIC_ENCRYPTION_CERT);

        KeyPair encryptionKeyPair = new KeyPair(publicEncryptionKey, privateEncryptionKey);

        KeyPair signingKeyPair = new KeyPair(publicKey, privateKey);
		IdaKeyStore keystore = new IdaKeyStore(signingKeyPair, Arrays.asList(encryptionKeyPair));
		IdaKeyStoreCredentialRetriever keyStoreCredentialRetriever = new IdaKeyStoreCredentialRetriever(keystore);
		SignatureWithKeyInfoFactory keyInfoFactory = new SignatureWithKeyInfoFactory(keyStoreCredentialRetriever, new SignatureRSASHA256(), new DigestSHA256(), "", "");

		Assertion assertion1 = anAssertion().withSignature(keyInfoFactory.createSignature()).build();
        Assertion assertion2 = anAssertion().withId(id).withSignature(keyInfoFactory.createSignature(id)).build();

        assertThat(assertion1.getSignature()).isNotNull();
        assertThat(assertion2.getSignature()).isNotNull();
    }
}

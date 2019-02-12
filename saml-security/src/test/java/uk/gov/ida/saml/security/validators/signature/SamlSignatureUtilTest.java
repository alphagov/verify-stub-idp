package uk.gov.ida.saml.security.validators.signature;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SAMLRuntimeException;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA256;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA256;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import uk.gov.ida.common.shared.security.PrivateKeyFactory;
import uk.gov.ida.common.shared.security.PublicKeyFactory;
import uk.gov.ida.common.shared.security.X509CertificateFactory;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.core.test.TestEntityIds;
import uk.gov.ida.saml.security.IdaKeyStore;
import uk.gov.ida.saml.security.IdaKeyStoreCredentialRetriever;
import uk.gov.ida.saml.security.SignatureFactory;
import uk.gov.ida.saml.security.saml.OpenSAMLRunner;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(OpenSAMLRunner.class)
public class SamlSignatureUtilTest {

    private SignatureFactory signatureFactory;

    @Before
    public void setup() throws Exception {
        PublicKeyFactory publicKeyFactory = new PublicKeyFactory(new X509CertificateFactory());
        PrivateKey privateKey = new PrivateKeyFactory().createPrivateKey(Base64.decodeBase64(TestCertificateStrings.PRIVATE_SIGNING_KEYS.get(
                TestEntityIds.HUB_ENTITY_ID)));
        PublicKey publicKey = publicKeyFactory.createPublicKey(TestCertificateStrings.getPrimaryPublicEncryptionCert(TestEntityIds.HUB_ENTITY_ID));

        PrivateKey privateEncryptionKey = new PrivateKeyFactory().createPrivateKey(Base64.decodeBase64(TestCertificateStrings.HUB_TEST_PRIVATE_ENCRYPTION_KEY));
        PublicKey publicEncryptionKey = publicKeyFactory.createPublicKey(TestCertificateStrings.HUB_TEST_PUBLIC_ENCRYPTION_CERT);

        KeyPair encryptionKeyPair = new KeyPair(publicEncryptionKey, privateEncryptionKey);

        KeyPair signingKeyPair = new KeyPair(publicKey, privateKey);
        IdaKeyStore keystore = new IdaKeyStore(signingKeyPair, Arrays.asList(encryptionKeyPair));
        IdaKeyStoreCredentialRetriever keyStoreCredentialRetriever = new IdaKeyStoreCredentialRetriever(keystore);
        signatureFactory = new SignatureFactory(keyStoreCredentialRetriever, new SignatureRSASHA256(), new DigestSHA256());
    }

    @Test
    public void isSignatureSigned_shouldThrowExceptionIfSignatureIsNotMarshalled() throws MarshallingException {
        Signature signature = signatureFactory.createSignature();
        try {
            assertThat(SamlSignatureUtil.isSignaturePresent(signature)).isEqualTo(false);
        } catch (SAMLRuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Signature has not been marshalled");
            return;
        }
        fail("Signature has not been marshalled");
    }

    @Test
    public void isSignatureSigned_shouldReturnFalseIfSignatureIsNotSigned() throws MarshallingException {
        Signature signature = signatureFactory.createSignature();
        XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(signature).marshall(signature);
        assertThat(SamlSignatureUtil.isSignaturePresent(signature)).isEqualTo(false);
    }

    @Test
    public void isSignatureSigned_shouldReturnTrueIfSignatureIsSigned() throws SignatureException, MarshallingException {
        Signature signature = signatureFactory.createSignature();
        XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(signature).marshall(signature);
        Signer.signObject(signature);
        assertThat(SamlSignatureUtil.isSignaturePresent(signature)).isEqualTo(true);
    }
}

package uk.gov.ida.saml.security;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import net.shibboleth.utilities.java.support.xml.XMLParserException;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.EncryptedElementType;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.xml.sax.SAXException;
import uk.gov.ida.common.shared.security.PrivateKeyFactory;
import uk.gov.ida.common.shared.security.PublicKeyFactory;
import uk.gov.ida.common.shared.security.X509CertificateFactory;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.core.test.TestEntityIds;
import uk.gov.ida.saml.security.saml.OpenSAMLRunner;
import uk.gov.ida.saml.security.saml.deserializers.SamlObjectParser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.fail;
import static uk.gov.ida.saml.security.saml.builders.EncryptedAssertionBuilder.anEncryptedAssertionBuilder;

@RunWith(OpenSAMLRunner.class)
public class DecrypterFactoryTest {

    private static final String OLD_HUB_PRIVATE_ENCRYPTION_KEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALyiEYiZvtPimmxUK8Vdl6GvaPqLPhSjeI/lk0mQbXQgmzqyb+vUTHO6Y2u5eusfPFsX6FSftdi1y7/k5aKVCBVQBewh6bzl/PcvOvDe/H99qvsrfTA25y1d2sdSqZBN/eFLEhJ8OT7+GuYVrSlNASxsNzngp7hJjfCKWKgEheHnAgMBAAECgYEAnXSnMCAt8w4XGs/TzaaHONaDyYdLZcziiTL4FBLz2liRWpix5efLfVqbPMORwAvNxLgbHfBGycNOdTqrGGBQYpKfLKB2xkAzDzQxe1XvpiKDF+6gnfaq4c6FNhMaS+llv3PVic3gVgWn+QnKG1k6eRL4oqyalp66rZp/RfLx6GkCQQDm2pm44T9xJ3+HFOVnLD1BnRXgCRAal9sJoR6PUtoQjuSKELrs4/e+UwItI7Ol/jLOFYrIDnpahES4O1snJHOFAkEA0S4iGp+zOcLgwq8FLTchi+gcMpjyYTfl0ypLDTqEVnFzE3ce/FhE4s4/JNHIaQydHPJXdsQQdv5+n7mAAyQtewJBAMw0yo4UEf6SJejjvxlItNb5kYQgADLF6WfXMiUt8N98xwSqT++EqH2fB+nODvfiqCZMP/s/c1PmdLNTLgqt39ECQFPxq4X7qLT5W7FFA1LN2QyILSiw8DPLdtNzGYNJhGocRQ3+s9SYp6xNEFH6Te66PSKsriTfMaxPHQmEK7cXAZkCQQCvxq4J2Qdc7Snt3RgUocLioQM8bjM7VePxO94rx5WN+yjYr9c2W5OgY3mKBUw7FbcCo7WzQ4rK1a2o/sknAfuY";
    private PrivateKey privateEncryptionKey;
    private PublicKey unusedPublicKey;


    @Before
    public void setup() throws Exception {
        uk.gov.ida.common.shared.security.PublicKeyFactory publicKeyFactory = new PublicKeyFactory(new X509CertificateFactory());
        privateEncryptionKey = new PrivateKeyFactory().createPrivateKey(Base64.decodeBase64(OLD_HUB_PRIVATE_ENCRYPTION_KEY));

        //We don't have the corresponding public key here. We intend to re-encrypt the test data and put both keys in this file.
        unusedPublicKey = publicKeyFactory.createPublicKey(TestCertificateStrings.STUB_IDP_PUBLIC_PRIMARY_CERT);
    }


    @Test
    public void createDecrypter_shouldCreateDecrypterWhichWorksWithInline() throws IOException, UnmarshallingException, XMLParserException, DecryptionException, ParserConfigurationException, SAXException {
        testDecrypt("encryptedAssertionRetrievalMethod.xml");
    }

    @Test
    public void createDecrypter_shouldCreateDecrypterWhichWorksWithRetrievalMethod() throws IOException, UnmarshallingException, XMLParserException, DecryptionException, ParserConfigurationException, SAXException {
        testDecrypt("encryptedAssertionInline.xml");
    }

    @Test
    public void createDecrpyter_shouldCreateDecrypterWhichReadsMultipleEncryptionKeys() throws DecryptionException {
        Credential primaryPrivateEncryptionKey = getPrivateKeyFor(TestEntityIds.HUB_ENTITY_ID);
        Credential secondaryPrivateEncryptionKey = getPrivateKeyFor(TestEntityIds.TEST_RP);
        Credential publicKeyForPrimaryPrivateEncryptionKey = getCredential(TestEntityIds.HUB_ENTITY_ID);
        Credential publicKeyForSecondaryPrivateEncryptionKey = getCredential(TestEntityIds.TEST_RP);

        EncryptedAssertion encryptedAssertion = anEncryptedAssertionBuilder().withId(UUID.randomUUID().toString()).withEncrypterCredential(publicKeyForPrimaryPrivateEncryptionKey).build();
        tryToDecrypt(primaryPrivateEncryptionKey, secondaryPrivateEncryptionKey, encryptedAssertion);

        encryptedAssertion = anEncryptedAssertionBuilder().withId(UUID.randomUUID().toString()).withEncrypterCredential(publicKeyForSecondaryPrivateEncryptionKey).build();
        tryToDecrypt(primaryPrivateEncryptionKey, secondaryPrivateEncryptionKey, encryptedAssertion);
    }

    private void tryToDecrypt(final Credential primaryPrivateEncryptionKey, final Credential secondaryPrivateEncryptionKey, final EncryptedAssertion encryptedAssertion) {
        Decrypter decrypter = new DecrypterFactory().createDecrypter(ImmutableList.of(primaryPrivateEncryptionKey, secondaryPrivateEncryptionKey));
        try {
            decrypter.decrypt(encryptedAssertion);
        } catch (DecryptionException e) {
            fail("Could not decrypt assertion: " + e.getMessage());
        }
    }

    private Credential getCredential(final String entityId) {
        HardCodedKeyStore keyStore = new HardCodedKeyStore(entityId);
        KeyStoreBackedEncryptionCredentialResolver keyStoreBackedEncryptionCredentialResolver = new KeyStoreBackedEncryptionCredentialResolver(keyStore);
        return keyStoreBackedEncryptionCredentialResolver.getEncryptingCredential(entityId);
    }

    private BasicCredential getPrivateKeyFor(final String entityId) {
        List<String> encryptionKeyStrings = TestCertificateStrings.PRIVATE_ENCRYPTION_KEYS.get(entityId);
        PrivateKey privateKey = new PrivateKeyFactory().createPrivateKey(Base64.decodeBase64(encryptionKeyStrings.get(0)));
        BasicCredential credential1 = new BasicCredential(unusedPublicKey, privateKey);
        return credential1;
    }

    private void testDecrypt(String fileName) throws IOException, UnmarshallingException, XMLParserException, DecryptionException, ParserConfigurationException, SAXException {
        DecrypterFactory decrypterFactory = new DecrypterFactory();

        String xml = CharStreams.toString(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName), "UTF-8"));

        EncryptedElementType xmlObject  = new SamlObjectParser().getSamlObject(xml);
        Credential basicCredential = new BasicCredential(unusedPublicKey, privateEncryptionKey);

        Decrypter decrypter = decrypterFactory.createDecrypter(Arrays.asList(basicCredential));

        decrypter.decryptData(xmlObject.getEncryptedData());
    }
}

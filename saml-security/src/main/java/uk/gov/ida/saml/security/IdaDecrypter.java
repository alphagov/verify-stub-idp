package uk.gov.ida.saml.security;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.EncryptedElementType;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class which implements SAML2-specific options for {@link org.opensaml.saml.saml2.core.EncryptedElementType} objects.
 *
 * <p>
 * For information on other parameters and options, and general XML Encryption issues,
 * see {@link org.opensaml.saml.saml2.encryption.Decrypter}.
 * </p>
 */
public class IdaDecrypter extends Decrypter {

    /** Class logger. */
    private static final Logger LOG = LoggerFactory.getLogger(IdaDecrypter.class);

    /**
     * Constructor.
     *
     * @param newResolver resolver for data encryption keys.
     * @param newKEKResolver resolver for key encryption keys.
     * @param newEncKeyResolver resolver for EncryptedKey elements
     */
    public IdaDecrypter(KeyInfoCredentialResolver newResolver, KeyInfoCredentialResolver newKEKResolver,
                        EncryptedKeyResolver newEncKeyResolver) {
        super(newResolver, newKEKResolver, newEncKeyResolver);
    }

    /**
     * Decrypt the specified EncryptedAssertion.
     *
     * @param encryptedAssertion the EncryptedAssertion to decrypt
     * @return an Assertion
     * @throws org.opensaml.xmlsec.encryption.support.DecryptionException thrown when decryption generates an error
     */
    public Assertion decrypt(EncryptedAssertion encryptedAssertion) throws DecryptionException {
        SAMLObject samlObject = decryptData(encryptedAssertion);
        if (! (samlObject instanceof Assertion)) {
            throw new DecryptionException("Decrypted SAMLObject was not an instance of Assertion");
        }
        return (Assertion) samlObject;
    }


    /**
     * Decrypt the specified instance of EncryptedElementType, and return it as an instance
     * of the specified QName.
     *
     *
     * @param encElement the EncryptedElementType to decrypt
     * @return the decrypted SAMLObject
     * @throws org.opensaml.xmlsec.encryption.support.DecryptionException thrown when decryption generates an error
     */
    private SAMLObject decryptData(EncryptedElementType encElement) throws DecryptionException {
        
        if (encElement.getEncryptedData() == null) {
            throw new DecryptionException("Element had no EncryptedData child");
        }
        
        XMLObject xmlObject;
        try {
            xmlObject = decryptData(encElement.getEncryptedData(), isRootInNewDocument());
        } catch (DecryptionException e) {
            LOG.error("SAML Decrypter encountered an error decrypting element content", e);
            throw e;
        }
        
        if (! (xmlObject instanceof SAMLObject)) {
            throw new DecryptionException("Decrypted XMLObject was not an instance of SAMLObject");
        }
        
        return (SAMLObject) xmlObject;
    }
}

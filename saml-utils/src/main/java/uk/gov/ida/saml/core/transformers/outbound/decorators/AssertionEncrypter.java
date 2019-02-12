package uk.gov.ida.saml.core.transformers.outbound.decorators;

import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.encryption.Encrypter;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import uk.gov.ida.saml.security.EncrypterFactory;
import uk.gov.ida.saml.security.KeyStoreBackedEncryptionCredentialResolver;

import static com.google.common.base.Throwables.propagate;

public class AssertionEncrypter {
    private final KeyStoreBackedEncryptionCredentialResolver credentialFactory;
    private final EncrypterFactory encrypterFactory;

    public AssertionEncrypter(EncrypterFactory encrypterFactory, KeyStoreBackedEncryptionCredentialResolver credentialFactory) {
        this.encrypterFactory = encrypterFactory;
        this.credentialFactory = credentialFactory;
    }

    public EncryptedAssertion encrypt(Assertion assertion, String entityId) {
        final Credential encryptingCredential = credentialFactory.getEncryptingCredential(entityId);
        final Encrypter encrypter = encrypterFactory.createEncrypter(encryptingCredential);
        try {
            return encrypter.encrypt(assertion);
        } catch (EncryptionException e) {
            throw propagate(e);
        }
    }
}

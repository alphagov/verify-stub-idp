package uk.gov.ida.saml.security;

import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;


public class KeyStoreBackedEncryptionCredentialResolver implements EncryptionCredentialResolver {
    private final EncryptionKeyStore encryptionKeyStore;


    public KeyStoreBackedEncryptionCredentialResolver(EncryptionKeyStore encryptionKeyStore) {
        this.encryptionKeyStore = encryptionKeyStore;
    }

    @Override
    public Credential getEncryptingCredential(String receiverId) {
        BasicCredential credential = new BasicCredential(encryptionKeyStore.getEncryptionKeyForEntity(receiverId));
        credential.setUsageType(UsageType.ENCRYPTION);
        return credential;
    }
}

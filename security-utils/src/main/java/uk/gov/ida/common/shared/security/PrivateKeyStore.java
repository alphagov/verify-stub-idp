package uk.gov.ida.common.shared.security;

import uk.gov.ida.common.shared.configuration.PrivateEncryptionKeys;
import uk.gov.ida.common.shared.configuration.PrivateSigningKey;

import java.security.PrivateKey;
import java.util.List;

@SuppressWarnings("unused")
public class PrivateKeyStore {
    private final PrivateKey signingPrivateKey;
    private final List<PrivateKey> encryptionPrivateKeys;

    public PrivateKeyStore(
            @PrivateSigningKey PrivateKey signingPrivateKey,
            @PrivateEncryptionKeys List<PrivateKey> encryptionPrivateKeys) {
        this.signingPrivateKey = signingPrivateKey;
        this.encryptionPrivateKeys = encryptionPrivateKeys;
    }

    public PrivateKey getSigningPrivateKey() {
        return signingPrivateKey;
    }

    public List<PrivateKey> getEncryptionPrivateKeys() {
        return encryptionPrivateKeys;
    }
}

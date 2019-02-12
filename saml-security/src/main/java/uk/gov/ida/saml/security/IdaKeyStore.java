package uk.gov.ida.saml.security;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.List;

public class IdaKeyStore {
    private final X509Certificate signingCertificate;
    private final KeyPair signingKeyPair;
    private final List<KeyPair> encryptionKeyPairs;

    public IdaKeyStore(KeyPair signingKeyPair, List<KeyPair> encryptionKeyPairs) {
        this(null, signingKeyPair, encryptionKeyPairs);
    }

    public IdaKeyStore(X509Certificate signingCertificate, KeyPair signingKeyPair, List<KeyPair> encryptionKeyPairs) {
        this.signingCertificate = signingCertificate;
        this.signingKeyPair = signingKeyPair;
        this.encryptionKeyPairs = encryptionKeyPairs;
    }

    public KeyPair getSigningKeyPair() {
        return signingKeyPair;
    }

    public List<KeyPair> getEncryptionKeyPairs() {
        return encryptionKeyPairs;
    }

    public X509Certificate getSigningCertificate() {
        return signingCertificate;
    }
}

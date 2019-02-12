package uk.gov.ida.saml.security;

import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.stream.Collectors;

public class IdaKeyStoreCredentialRetriever {

    private final IdaKeyStore keyStore;


    public IdaKeyStoreCredentialRetriever(IdaKeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public Credential getSigningCredential() {
        UsageType usageType = UsageType.SIGNING;
        KeyPair keyPair = keyStore.getSigningKeyPair();
        BasicCredential credential = buildCredential(keyPair.getPublic(), keyPair.getPrivate(), usageType);
        return credential;
    }

    public X509Certificate getSigningCertificate() {
        return keyStore.getSigningCertificate();
    }

    public List<Credential> getDecryptingCredentials() {
        return getCredentials(UsageType.ENCRYPTION, keyStore.getEncryptionKeyPairs());
    }

    private List<Credential> getCredentials(final UsageType usageType, List<KeyPair> keyPairs) {
        return keyPairs.stream()
                .map(keyPair -> buildCredential(keyPair.getPublic(), keyPair.getPrivate(), usageType))
                .collect(Collectors.toList());
    }

    private BasicCredential buildCredential(PublicKey publicKey, PrivateKey privateKey, UsageType usageType) {
        BasicCredential credential = new BasicCredential(publicKey, privateKey);
        credential.setUsageType(usageType);
        return credential;
    }

}

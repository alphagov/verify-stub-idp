package keystore.builders;

import keystore.CertificateEntry;
import keystore.KeyEntry;
import keystore.KeyStoreRule;

import java.io.File;
import java.util.List;

public class KeyStoreRuleBuilder {
    private KeyStoreResourceBuilder keyStoreResourceBuilder = KeyStoreResourceBuilder.aKeyStoreResource();

    public KeyStoreRuleBuilder withKeys(List<KeyEntry> keys) {
        this.keyStoreResourceBuilder.withKeys(keys);
        return this;
    }

    public KeyStoreRuleBuilder withKey(String alias, String key, String certificateChain) {
        this.keyStoreResourceBuilder.withKey(alias, key, certificateChain);
        return this;
    }

    public KeyStoreRuleBuilder withCertificates(List<CertificateEntry> certificates) {
        this.keyStoreResourceBuilder.withCertificates(certificates);
        return this;
    }

    public KeyStoreRuleBuilder withCertificate(String alias, String certificate) {
        this.keyStoreResourceBuilder.withCertificate(alias, certificate);
        return this;
    }

    public KeyStoreRuleBuilder withFile(File file) {
        this.keyStoreResourceBuilder.withFile(file);
        return this;
    }

    public static KeyStoreRuleBuilder aKeyStoreRule() {
       return new KeyStoreRuleBuilder();
    }

    public KeyStoreRule build() {
        return new KeyStoreRule(keyStoreResourceBuilder.build());
    }
}
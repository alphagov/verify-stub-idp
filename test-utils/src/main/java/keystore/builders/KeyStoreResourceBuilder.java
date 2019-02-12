package keystore.builders;

import keystore.CertificateEntry;
import keystore.KeyEntry;
import keystore.KeyStoreResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Throwables.propagate;

public class KeyStoreResourceBuilder {
    private List<KeyEntry> keys = new ArrayList<>();
    private List<CertificateEntry> certificates = new ArrayList<>();
    private File file;

    private KeyStoreResourceBuilder() {
    }

    public static KeyStoreResourceBuilder aKeyStoreResource() {
        return new KeyStoreResourceBuilder();
    }

    public KeyStoreResourceBuilder withFile(File file) {
        this.file = file;
        return this;
    }

    public KeyStoreResourceBuilder withKeys(List<KeyEntry> keys) {
        this.keys = keys;
        return this;
    }

    public KeyStoreResourceBuilder withKey(String alias, String key, String certificateChain) {
        this.keys.add(new KeyEntry(alias, key, certificateChain));
        return this;
    }

    public KeyStoreResourceBuilder withCertificates(List<CertificateEntry> certificates) {
        this.certificates = certificates;
        return this;
    }

    public KeyStoreResourceBuilder withCertificate(String alias, String certificate) {
        this.certificates.add(new CertificateEntry(alias, certificate));
        return this;
    }

    public KeyStoreResource build() {
        if(file == null) {
            try {
                file = File.createTempFile("test-keystore", null, null);
            } catch (IOException e) {
                throw propagate(e);
            }
        }
        return new KeyStoreResource(file, keys, certificates);
    }
}
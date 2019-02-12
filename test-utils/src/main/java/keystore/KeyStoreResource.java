package keystore;

import com.google.common.base.Throwables;
import helpers.ManagedFileResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

public class KeyStoreResource implements ManagedFileResource {
    private final static String password = "password";
    private final File file;
    private final List<KeyEntry> keys;
    private final List<CertificateEntry> certificates;

    private KeyStore keyStore;

    public KeyStoreResource(File file, List<KeyEntry> keys, List<CertificateEntry> certificates) {
        this.file = file;
        this.keys = keys;
        this.certificates = certificates;
    }
    public String getPassword() {
        return password;
    }

    public File getFile() {
        return file;
    }

    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    @Override
    public void create() {
        loadKeyStore();
        setKeys();
        setCertificates();
        writeKeyStore();
    }

    @Override
    public void delete() {
        file.delete();
    }

    private KeyStore loadKeyStore() {
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
        } catch (IOException | NoSuchAlgorithmException | KeyStoreException | CertificateException e) {
            throw Throwables.propagate(e);
        }
        return keyStore;
    }

    private void setKeys() {
        X509CertificateFactory x509CertificateFactory = new X509CertificateFactory();
        PrivateKeyFactory privateKeyFactory = new PrivateKeyFactory();
        keys.stream().forEach((entry) -> {
            X509Certificate[] x509Certificates = Arrays
                    .asList(entry.getCertificates())
                    .stream()
                    .map(x509CertificateFactory::createCertificate)
                    .toArray(X509Certificate[]::new);
            PrivateKey key = privateKeyFactory.createPrivateKey(entry.getKey().getBytes());
            try {
                keyStore.setKeyEntry(entry.getAlias(), key, getPassword().toCharArray(), x509Certificates);
            } catch (KeyStoreException e) {
                throw Throwables.propagate(e);
            }
        });
    }

    private void setCertificates() {
        X509CertificateFactory x509CertificateFactory = new X509CertificateFactory();
        certificates.stream().forEach((entry) -> {
            X509Certificate x509Certificate = x509CertificateFactory.createCertificate(entry.getCertificate());
            try {
                keyStore.setCertificateEntry(entry.getAlias(), x509Certificate);
            } catch (KeyStoreException e) {
                throw Throwables.propagate(e);
            }
        });
    }

    private void writeKeyStore() {
        try (FileOutputStream fos = new FileOutputStream(getFile());) {
            keyStore.store(fos, getPassword().toCharArray());
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            throw Throwables.propagate(e);
        }
    }
}

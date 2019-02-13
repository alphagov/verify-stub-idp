package uk.gov.ida.truststore;

import com.google.common.base.Throwables;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class KeyStoreLoader {

    @Inject
    public KeyStoreLoader() { }

    public KeyStore load(String uri, String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] charPassword = password.toCharArray();
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(uri);
                keyStore.load(inputStream, charPassword);
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return keyStore;
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw Throwables.propagate(e);
        }
    }
}

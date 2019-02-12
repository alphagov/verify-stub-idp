package uk.gov.ida.saml.metadata.factories;

import com.google.common.base.Throwables;
import uk.gov.ida.saml.metadata.KeyStoreLoader;
import uk.gov.ida.saml.metadata.exception.EmptyTrustStoreException;

import javax.inject.Provider;
import java.security.KeyStore;
import java.security.KeyStoreException;

public class MetadataTrustStoreProvider implements Provider<KeyStore> {

    private KeyStoreLoader keyStoreLoader;
    private String uri;
    private String password;

    public MetadataTrustStoreProvider(KeyStoreLoader keyStoreLoader, String uri, String password) {
        this.keyStoreLoader = keyStoreLoader;
        this.uri = uri;
        this.password = password;
    }

    @Override
    public KeyStore get() {
        KeyStore trustStore = keyStoreLoader.load(uri, password);
        int trustStoreSize = 0;
        try {
            trustStoreSize = trustStore.size();
        } catch (KeyStoreException e) {
            Throwables.propagate(e);
        }
        if (trustStoreSize == 0) {
            throw new EmptyTrustStoreException();
        }
        return trustStore;
    }
}

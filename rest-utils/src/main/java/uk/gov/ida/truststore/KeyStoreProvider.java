package uk.gov.ida.truststore;


import javax.inject.Inject;
import javax.inject.Provider;

import java.security.KeyStore;

public class KeyStoreProvider implements Provider<KeyStore> {

    private final ClientTrustStoreConfiguration configuration;
    private final KeyStoreLoader keyStoreLoader;

    @Inject
    public KeyStoreProvider(ClientTrustStoreConfiguration configuration, KeyStoreLoader keyStoreLoader) {
        this.configuration = configuration;
        this.keyStoreLoader = keyStoreLoader;
    }

    @Override
    public KeyStore get() {
        return keyStoreLoader.load(configuration.getPath(), configuration.getPassword());
    }
}

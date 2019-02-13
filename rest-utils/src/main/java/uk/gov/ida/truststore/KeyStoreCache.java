package uk.gov.ida.truststore;


import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.security.KeyStore;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Singleton
public class KeyStoreCache {


    private final Cache<String, KeyStore> trustStoreCache;
    private final KeyStoreLoader keyStoreLoader;
    @Inject
    public KeyStoreCache(KeyStoreLoader keyStoreLoader) {
        this.keyStoreLoader = keyStoreLoader;
        trustStoreCache = CacheBuilder.newBuilder().build();
    }

    public KeyStore get(final ClientTrustStoreConfiguration configuration) {

        final String trustStorePath = configuration.getPath();
        final String password = configuration.getPassword();

        try {
            return trustStoreCache.get(trustStorePath, new Callable<KeyStore>() {
                @Override
                public KeyStore call() throws Exception {
                    return keyStoreLoader.load(trustStorePath, password);
                }
            });
        } catch (ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }

}

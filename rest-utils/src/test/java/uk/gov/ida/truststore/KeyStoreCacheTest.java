package uk.gov.ida.truststore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.security.KeyStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KeyStoreCacheTest {

    @Mock
    private KeyStoreLoader keyStoreLoader;

    @Mock
    private KeyStore keyStore;

    private KeyStoreCache keyStoreCache;

    private ClientTrustStoreConfiguration configuration;


    @Before
    public void setUp(){
        keyStoreCache = new KeyStoreCache(keyStoreLoader);
        configuration = ClientTrustStoreConfigurationBuilder.aClientTrustStoreConfiguration().build();
    }

    @Test
    public void shouldLoadKeyStoreIfNotAlreadyLoaded() throws Exception {
        when(keyStoreLoader.load(configuration.getPath(), configuration.getPassword())).thenReturn(keyStore);
        KeyStore keyStore = keyStoreCache.get(configuration);
        assertThat(keyStore).isEqualTo(this.keyStore);
    }

    @Test
    public void shouldOnlyHaveToLoadTheKeyStoreOnce() throws Exception {
        when(keyStoreLoader.load(configuration.getPath(), configuration.getPassword())).thenReturn(keyStore);
        keyStoreCache.get(configuration);
        keyStoreCache.get(configuration);
        verify(keyStoreLoader, times(1)).load(configuration.getPath(), configuration.getPassword());
    }
}

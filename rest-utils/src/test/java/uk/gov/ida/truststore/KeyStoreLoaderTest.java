package uk.gov.ida.truststore;

import org.junit.Test;

import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyStoreLoaderTest {
    
    @Test
    public void should_loadCertificatesFromTrustStore() throws KeyStoreException {
        URL resourcePath = getClass().getClassLoader().getResource("ida_truststore.ts");
        KeyStore keyStore = new KeyStoreLoader().load(resourcePath.getPath(), "puppet");
        assertThat(keyStore.size()).isEqualTo(2);
    }
}

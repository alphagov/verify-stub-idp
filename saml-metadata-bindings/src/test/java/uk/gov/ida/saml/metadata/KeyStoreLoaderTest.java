package uk.gov.ida.saml.metadata;

import helpers.ResourceHelpers;
import org.junit.Test;

import java.security.KeyStore;

import static org.junit.Assert.assertNotNull;

public class KeyStoreLoaderTest {
    private KeyStoreLoader keyStoreLoader = new KeyStoreLoader();

    @Test
    public void testLoadFromString() throws Exception {
        KeyStore keyStore = keyStoreLoader.load(ResourceHelpers.resourceFilePath("test-truststore.ts"), "puppet");
        assertNotNull(keyStore);
    }

    @Test
    public void testLoadFromStream() throws Exception {
        KeyStore keyStore = keyStoreLoader.load(this.getClass().getResourceAsStream("/test-truststore.ts"), "puppet");
        assertNotNull(keyStore);
    }
}

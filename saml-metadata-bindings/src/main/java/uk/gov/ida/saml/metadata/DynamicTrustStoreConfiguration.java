package uk.gov.ida.saml.metadata;

import java.security.KeyStore;

public class DynamicTrustStoreConfiguration extends TrustStoreConfiguration {

    private final KeyStore trustStore;

    public DynamicTrustStoreConfiguration(KeyStore trustStore) {
        this.trustStore = trustStore;
    }

    @Override
    public KeyStore getTrustStore() {
        return trustStore;
    }
}

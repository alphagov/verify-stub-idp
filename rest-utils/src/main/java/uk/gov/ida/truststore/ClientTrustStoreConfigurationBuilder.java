package uk.gov.ida.truststore;

public class ClientTrustStoreConfigurationBuilder {

    public static ClientTrustStoreConfigurationBuilder aClientTrustStoreConfiguration() {
        return new ClientTrustStoreConfigurationBuilder();
    }

    public ClientTrustStoreConfiguration build(){
        return new TestClientTrustStoreConfiguration("trustStorePath", "password");
    }

    private class TestClientTrustStoreConfiguration extends ClientTrustStoreConfiguration{
        private TestClientTrustStoreConfiguration(String trustStorePath, String password) {
            this.path = trustStorePath;
            this.password = password;
        }

    }
}

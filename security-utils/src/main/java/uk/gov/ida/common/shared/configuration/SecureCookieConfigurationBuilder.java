package uk.gov.ida.common.shared.configuration;


import static uk.gov.ida.common.shared.configuration.KeyConfigurationBuilder.aKeyConfiguration;

public class SecureCookieConfigurationBuilder {

    public static SecureCookieConfigurationBuilder aSecureCookieConfiguration() {
        return new SecureCookieConfigurationBuilder();
    }

    public SecureCookieConfiguration build() {
        return new TestSecureCookieConfiguration(
                aKeyConfiguration().build(),
                false);
    }

    private static class TestSecureCookieConfiguration extends SecureCookieConfiguration {
        private TestSecureCookieConfiguration(
                KeyConfiguration keyConfiguration,
                boolean secure) {
            this.keyConfiguration = keyConfiguration;
            this.secure = secure;
        }
    }
}

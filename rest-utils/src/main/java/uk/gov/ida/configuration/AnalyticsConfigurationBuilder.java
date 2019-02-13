package uk.gov.ida.configuration;

public class AnalyticsConfigurationBuilder {

    private boolean enabled = true;

    public static AnalyticsConfigurationBuilder anAnalyticsConfiguration() {
        return new AnalyticsConfigurationBuilder();
    }

    public AnalyticsConfiguration build() {
        return new TestAnalyticsConfiguration(enabled, 1, "https://localhost:1235/piwik.php", "https://localhost:1235/piwik.php");

    }

    public AnalyticsConfigurationBuilder setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    private static class TestAnalyticsConfiguration extends AnalyticsConfiguration {
        private TestAnalyticsConfiguration(boolean enabled, Integer siteId, String piwikBaseUrl, String piwikServerSideUrl) {
            this.enabled = enabled;
            this.siteId = siteId;
            this.piwikBaseUrl = piwikBaseUrl;
            this.piwikServerSideUrl = piwikServerSideUrl;
        }
    }
}

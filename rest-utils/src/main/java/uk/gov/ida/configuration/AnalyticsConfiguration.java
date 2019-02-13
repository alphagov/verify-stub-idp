package uk.gov.ida.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.String;

public class AnalyticsConfiguration {

    protected AnalyticsConfiguration() {

    }

    @NotNull
    @Valid
    protected Boolean enabled;

    @Valid
    protected Integer siteId;

    @Valid
    protected String piwikBaseUrl;

    @Valid
    protected String piwikServerSideUrl;

    public String getPiwikServerSideUrl() {
        return piwikServerSideUrl;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public String getPiwikBaseUrl() {
        return piwikBaseUrl;
    }

    public boolean getEnabled() {
        return enabled;
    }
}

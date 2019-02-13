package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class SecureCookieConfiguration {

    protected SecureCookieConfiguration() {
    }

    @Valid
    @NotNull
    @JsonProperty
    protected KeyConfiguration keyConfiguration;

    @Valid
    @NotNull
    @JsonProperty
    protected Boolean secure;

    public KeyConfiguration getKeyConfiguration() {
        return keyConfiguration;
    }

    public Boolean isSecure() {
        return secure;
    }
}

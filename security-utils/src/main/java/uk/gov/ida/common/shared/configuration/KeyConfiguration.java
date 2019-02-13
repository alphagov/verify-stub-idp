package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class KeyConfiguration {

    protected KeyConfiguration() {
    }

    @Valid
    @NotNull
    @Size(min = 1)
    @JsonProperty
    protected String keyUri;

    public String getKeyUri() {
        return keyUri;
    }
}

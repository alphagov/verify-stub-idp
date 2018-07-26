package uk.gov.ida.stub.idp.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseConfiguration {

    @NotNull
    @JsonProperty("url")
    private String url;

    public String getUrl() {
        return url;
    }
}

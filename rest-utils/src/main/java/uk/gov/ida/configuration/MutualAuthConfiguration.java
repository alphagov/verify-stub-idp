package uk.gov.ida.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.io.File;

public class MutualAuthConfiguration {
    @JsonProperty
    @NotNull
    protected File keyStoreFile;

    @JsonProperty
    @NotNull
    protected String keyStorePassword;

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public File getKeyStoreFile() {
        return keyStoreFile;
    }
}

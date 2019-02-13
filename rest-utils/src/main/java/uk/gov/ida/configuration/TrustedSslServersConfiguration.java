package uk.gov.ida.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import java.io.File;

public class TrustedSslServersConfiguration {

    @JsonProperty
    @Valid
    protected File trustStore;

    @JsonProperty
    @Valid
    protected String password;

    public File getTrustStore() {
        return trustStore;
    }

    public char [] getPassword() {
        return password.toCharArray();
    }
}

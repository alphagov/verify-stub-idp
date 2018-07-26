package uk.gov.ida.stub.idp.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class UserCredentials {
    protected UserCredentials() {
    }

    @NotNull
    @Valid
    @JsonProperty
    protected String user;

    @NotNull
    @Valid
    @JsonProperty
    protected String password;


    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}

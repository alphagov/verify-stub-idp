package uk.gov.ida.truststore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientTrustStoreConfiguration {

    protected ClientTrustStoreConfiguration() {
    }

    public ClientTrustStoreConfiguration(String path, String password) {
        this.path = path;
        this.password = password;
    }

    @Valid
    @NotNull
    protected String path;

    @Valid
    @NotNull
    @Size(min = 1)
    protected String password;

    public String getPath() {
        return path;
    }

    public String getPassword() {
        return password;
    }
}

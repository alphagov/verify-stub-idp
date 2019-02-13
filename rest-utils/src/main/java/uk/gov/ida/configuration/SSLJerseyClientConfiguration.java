package uk.gov.ida.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientConfiguration;

import javax.validation.Valid;

public class SSLJerseyClientConfiguration extends JerseyClientConfiguration implements SSLContextConfiguration{
    @JsonProperty
    public MutualAuthConfiguration mutualAuth = null;

    @JsonProperty
    @Valid
    public TrustedSslServersConfiguration trustedSslServers = null;

    @Override
    public TrustedSslServersConfiguration getTrustedSslServers() {
        return trustedSslServers;
    }

    @Override
    public MutualAuthConfiguration getMutualAuth() {
        return mutualAuth;
    }
}

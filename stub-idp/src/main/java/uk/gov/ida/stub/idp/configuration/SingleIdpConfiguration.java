package uk.gov.ida.stub.idp.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

public class SingleIdpConfiguration {

    @Valid
    @JsonProperty
    private boolean enabled = false;

    @NotNull
    @Valid
    @JsonProperty
    private URI serviceListUri = URI.create("http://NotUsed");

    @NotNull
    @Valid
    @JsonProperty
    private JerseyClientConfiguration serviceListClient = new JerseyClientConfiguration();

    public URI getServiceListUrl() { return serviceListUri; }

    public JerseyClientConfiguration getServiceListClient() { return serviceListClient; }

    public boolean isEnabled() { return enabled; }
}


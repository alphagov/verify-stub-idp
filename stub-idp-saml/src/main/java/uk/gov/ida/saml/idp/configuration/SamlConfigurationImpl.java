package uk.gov.ida.saml.idp.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

public class SamlConfigurationImpl implements SamlConfiguration {
    protected SamlConfigurationImpl() {
    }

    @Valid
    @NotNull
    @JsonProperty
    protected String entityId;

    @Valid
    @JsonProperty
    protected URI expectedDestination = URI.create("http://configure.me/if/i/fail");

    @Override
    public URI getExpectedDestinationHost() {
        return expectedDestination;
    }

    public String getEntityId() {
        return entityId;
    }
}

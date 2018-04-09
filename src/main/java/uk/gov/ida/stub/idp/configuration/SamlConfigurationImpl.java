package uk.gov.ida.stub.idp.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.ida.saml.idp.configuration.SamlConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

public class SamlConfigurationImpl implements SamlConfiguration {
    protected SamlConfigurationImpl() {
    }

    public SamlConfigurationImpl(String entityId, URI expectedDestination) {
        this.entityId = entityId;
        this.expectedDestination = expectedDestination;
    }

    @Valid
    @NotNull
    @JsonProperty
    protected String entityId;

    @Valid
    @JsonProperty
    protected URI expectedDestination = URI.create("http://configure.me/if/i/fail");

    public String getEntityId() {
        return entityId;
    }

    public URI getExpectedDestinationHost() {
        return expectedDestination;

    }

}

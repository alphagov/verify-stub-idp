package uk.gov.ida.saml.metadata;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientConfiguration;
import uk.gov.ida.saml.metadata.factories.MetadataTrustStoreProvider;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.security.KeyStore;

/**
 * Uses a flat structure to parse metadata details
 *
 * @deprecated Use {@link TrustStoreBackedMetadataConfiguration} instead. This will change configs
 * to use a nested trust store object instead of having path and password as part of the metadata configuration.
 */
@Deprecated
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrustStorePathMetadataConfiguration extends MetadataConfiguration {

    /*
     * TrustStore configuration is used to do certificate chain validation when loading metadata
     */
    @NotNull
    @Valid
    private String trustStorePath;

    @NotNull
    @Valid
    private String trustStorePassword;

    @JsonCreator
    public TrustStorePathMetadataConfiguration(
            @JsonProperty("uri") @JsonAlias({ "url" }) URI uri,
            @JsonProperty("minRefreshDelay") Long minRefreshDelay,
            @JsonProperty("maxRefreshDelay") Long maxRefreshDelay,
            @JsonProperty("expectedEntityId") String expectedEntityId,
            @JsonProperty("client") JerseyClientConfiguration client,
            @JsonProperty("jerseyClientName") @JsonAlias({ "client" }) String jerseyClientName,
            @JsonProperty("hubFederationId") String hubFederationId,
            @JsonProperty("trustStorePath") String trustStorePath,
            @JsonProperty("trustStorePassword") String trustStorePassword
    ) {
        super(uri, minRefreshDelay, maxRefreshDelay, expectedEntityId, client, jerseyClientName, hubFederationId);
        this.trustStorePath = trustStorePath;
        this.trustStorePassword = trustStorePassword;
    }

    @Override
    public KeyStore getTrustStore() {
        return new MetadataTrustStoreProvider(new KeyStoreLoader(), trustStorePath, trustStorePassword).get();
    }
}

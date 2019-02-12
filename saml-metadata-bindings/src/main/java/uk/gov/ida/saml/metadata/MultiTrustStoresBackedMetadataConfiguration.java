package uk.gov.ida.saml.metadata;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.security.KeyStore;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MultiTrustStoresBackedMetadataConfiguration extends TrustStoreBackedMetadataConfiguration {

    @NotNull
    @Valid
    private TrustStoreConfiguration hubTrustStore;

    @NotNull
    @Valid
    private TrustStoreConfiguration idpTrustStore;

    @JsonCreator
    public MultiTrustStoresBackedMetadataConfiguration(
        @JsonProperty("uri") @JsonAlias({ "url" }) URI uri,
        @JsonProperty("minRefreshDelay") Long minRefreshDelay,
        @JsonProperty("maxRefreshDelay") Long maxRefreshDelay,
        @JsonProperty("expectedEntityId") String expectedEntityId,
        @JsonProperty("client") JerseyClientConfiguration client,
        @JsonProperty("jerseyClientName") String jerseyClientName,
        @JsonProperty("hubFederationId") String hubFederationId,
        @JsonProperty("trustStore") TrustStoreConfiguration trustStore,
        @JsonProperty("hubTrustStore") TrustStoreConfiguration hubTrustStore,
        @JsonProperty("idpTrustStore") TrustStoreConfiguration idpTrustStore) {

        super(uri, minRefreshDelay, maxRefreshDelay, expectedEntityId, client, jerseyClientName, hubFederationId, trustStore);
        this.hubTrustStore = hubTrustStore;
        this.idpTrustStore = idpTrustStore;
    }

    @Override
    public Optional<KeyStore> getHubTrustStore() {
        return Optional.of(hubTrustStore.getTrustStore());
    }

    @Override
    public Optional<KeyStore> getIdpTrustStore() {
        return Optional.of(idpTrustStore.getTrustStore());
    }
}

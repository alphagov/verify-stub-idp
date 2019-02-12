package uk.gov.ida.saml.metadata;

import io.dropwizard.client.JerseyClientConfiguration;

import java.net.URI;
import java.security.KeyStore;
import java.util.Optional;

public interface MetadataResolverConfiguration {

    KeyStore getTrustStore();

    default Optional<KeyStore> getHubTrustStore() {
        return Optional.empty();
    }

    default Optional<KeyStore> getIdpTrustStore() {
        return Optional.empty();
    }

    URI getUri();

    Long getMinRefreshDelay();

    Long getMaxRefreshDelay();

    String getExpectedEntityId();

    JerseyClientConfiguration getJerseyClientConfiguration();

    String getJerseyClientName();

    String getHubFederationId();
}

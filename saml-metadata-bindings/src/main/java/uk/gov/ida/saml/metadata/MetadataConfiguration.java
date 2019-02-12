package uk.gov.ida.saml.metadata;

import io.dropwizard.client.JerseyClientConfiguration;

import java.net.URI;
import java.util.Optional;

public abstract class MetadataConfiguration implements MetadataResolverConfiguration {

    /* HTTP{S} URL the SAML metadata can be loaded from */
    private URI uri;

    /* Used to set {@link org.opensaml.saml2.metadata.provider.AbstractReloadingMetadataProvider#minRefreshDelay} */
    private Long minRefreshDelay;

    /* Used to set {@link org.opensaml.saml2.metadata.provider.AbstractReloadingMetadataProvider#maxRefreshDelay} */
    private Long maxRefreshDelay;

    /*
     * What entityId can be expected to reliably appear in the SAML metadata?
     * Used to provide a healthcheck {@link uk.gov.ida.saml.dropwizard.metadata.MetadataHealthCheck}
     */
    private String expectedEntityId;

    private JerseyClientConfiguration client;
    private String jerseyClientName;
    private String hubFederationId;

    public MetadataConfiguration(URI uri,
        Long minRefreshDelay,
        Long maxRefreshDelay,
        String expectedEntityId,
        JerseyClientConfiguration client,
        String jerseyClientName,
        String hubFederationId
    ) {
        this.uri = uri;
        this.minRefreshDelay = Optional.ofNullable(minRefreshDelay).orElse(60000L);
        this.maxRefreshDelay = Optional.ofNullable(maxRefreshDelay).orElse(600000L);
        this.expectedEntityId = Optional.ofNullable(expectedEntityId).orElse("https://signin.service.gov.uk");
        this.client = Optional.ofNullable(client).orElse(new JerseyClientConfiguration());
        this.jerseyClientName = Optional.ofNullable(jerseyClientName).orElse("MetadataClient");
        this.hubFederationId = Optional.ofNullable(hubFederationId).orElse("VERIFY-FEDERATION");
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public Long getMinRefreshDelay() {
        return minRefreshDelay;
    }

    @Override
    public Long getMaxRefreshDelay() {
        return maxRefreshDelay;
    }

    @Override
    public String getExpectedEntityId() {
        return expectedEntityId;
    }

    @Override
    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return client;
    }

    @Override
    public String getJerseyClientName() {
        return jerseyClientName;
    }

    @Override
    public String getHubFederationId() {
        return hubFederationId;
    }
}

package uk.gov.ida.stub.idp.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.util.Duration;
import uk.gov.ida.cache.AssetCacheConfiguration;
import uk.gov.ida.common.ServiceInfoConfiguration;
import uk.gov.ida.common.shared.configuration.SecureCookieConfiguration;
import uk.gov.ida.configuration.ServiceNameConfiguration;
import uk.gov.ida.saml.idp.configuration.SamlConfiguration;
import uk.gov.ida.saml.metadata.MetadataResolverConfiguration;
import uk.gov.ida.saml.metadata.TrustStoreBackedMetadataConfiguration;
import uk.gov.ida.stub.idp.repositories.reaper.StaleSessionReaperConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StubIdpConfiguration extends Configuration implements
        AssertionLifetimeConfiguration,
        AssetCacheConfiguration,
        ServiceNameConfiguration {

    @Valid
    @JsonProperty
    private String assetsCacheDuration = "0";

    @Valid
    @JsonProperty
    private boolean shouldCacheAssets = false;

    @Valid
    @NotNull
    @JsonProperty
    protected Duration assertionLifetime;

    @JsonProperty
    @NotNull
    @Valid
    protected ServiceInfoConfiguration serviceInfo;

    @NotNull
    @Valid
    @JsonProperty
    protected SamlConfigurationImpl saml;

    @NotNull
    @Valid
    @JsonProperty
    protected SigningKeyPairConfiguration signingKeyPairConfiguration;

    @Valid
    @JsonProperty
    @NotNull
    protected Boolean basicAuthEnabledForUserResource = true;

    @NotNull
    @Valid
    @JsonProperty
    protected String stubIdpsYmlFileLocation;

    @NotNull
    @Valid
    @JsonProperty
    protected Duration stubIdpYmlFileRefresh;

    @NotNull
    @Valid
    @JsonProperty
    protected TrustStoreBackedMetadataConfiguration metadata;

    @NotNull
    @Valid
    @JsonProperty
    protected String hubEntityId = "https://signin.service.gov.uk";

    // to generate a new cookie.key use the command `dd if=/dev/random count=1 bs=64 | base64`
    @Valid
    @JsonProperty
    protected SecureCookieConfiguration secureCookieConfiguration = null;

    @NotNull
    @Valid
    @JsonProperty
    private EuropeanIdentityConfiguration europeanIdentity;

    @NotNull
    @Valid
    @JsonProperty("database")
    private DatabaseConfiguration databaseConfiguration;

    @Valid
    @NotNull
    private boolean singleIdpJourneyEnabled = false;

    @Valid
    @JsonProperty
    private SingleIdpConfiguration singleIdpJourney;

    @NotNull
    @Valid
    @JsonProperty
    private StaleSessionReaperConfiguration staleSessionReaperConfiguration = new StaleSessionReaperConfiguration();

    protected StubIdpConfiguration() {
    }

    public String getHubEntityId() {
        return hubEntityId;
    }

    @Override
    public Duration getAssertionLifetime() {
        return assertionLifetime;
    }

    @Override
    public String getServiceName() {
        return serviceInfo.getName();
    }

    public SamlConfiguration getSamlConfiguration() {
        return saml;
    }

    public boolean isBasicAuthEnabledForUserResource() {
        return basicAuthEnabledForUserResource;
    }

    public String getStubIdpsYmlFileLocation() {
        return stubIdpsYmlFileLocation;
    }

    public Duration getStubIdpYmlFileRefresh() {
        return stubIdpYmlFileRefresh;
    }

    public String getAssetsCacheDuration() {
        return assetsCacheDuration;
    }

    public boolean shouldCacheAssets() {
        return shouldCacheAssets;
    }

    public SigningKeyPairConfiguration getSigningKeyPairConfiguration(){
        return signingKeyPairConfiguration;
    }

    public MetadataResolverConfiguration getMetadataConfiguration() {
        return metadata;
    }

    public SecureCookieConfiguration getSecureCookieConfiguration() {
        return secureCookieConfiguration;
    }

    public EuropeanIdentityConfiguration getEuropeanIdentityConfiguration() {
        return europeanIdentity;
    }

    public DatabaseConfiguration getDatabaseConfiguration() {
        return databaseConfiguration;
    }

    public SingleIdpConfiguration getSingleIdpJourneyConfiguration() { return singleIdpJourney; }

    public StaleSessionReaperConfiguration getStaleSessionReaperConfiguration() {
        return staleSessionReaperConfiguration;
    }

    public boolean isSingleIdpJourneyEnabled() {
        return singleIdpJourneyEnabled;
    }
}

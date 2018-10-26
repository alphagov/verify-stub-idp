package uk.gov.ida.stub.idp.domain.factories;

import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.stub.idp.configuration.IdpStubsConfiguration;
import uk.gov.ida.stub.idp.configuration.StubIdpConfiguration;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;

public class IdpStubsConfigurationFactory {

    private static final Logger LOG = LoggerFactory.getLogger(IdpStubsConfigurationFactory.class);

    private final StubIdpConfiguration stubIdpConfiguration;
    private final ConfigurationFactory<IdpStubsConfiguration> configurationFactory;
    private final ConfigurationSourceProvider configurationSourceProvider;

    @Inject
    public IdpStubsConfigurationFactory(StubIdpConfiguration stubIdpConfiguration, ConfigurationFactory<IdpStubsConfiguration> configurationFactory, ConfigurationSourceProvider configurationSourceProvider) {
        this.stubIdpConfiguration = stubIdpConfiguration;
        this.configurationFactory = configurationFactory;
        this.configurationSourceProvider = configurationSourceProvider;
    }

    public Optional<IdpStubsConfiguration> tryBuildDefault() {
        try {
            return Optional.of(build(stubIdpConfiguration.getStubIdpsYmlFileLocation()));
        } catch (IOException | ConfigurationException e) {
            LOG.error("Error parsing configuration file, stubs remain unchanged", e);
        }

        return Optional.empty();
    }

    public IdpStubsConfiguration build(String stubIdpsYmlFileLocation) throws IOException, ConfigurationException {
        return configurationFactory.build(configurationSourceProvider, stubIdpsYmlFileLocation);
    }
}

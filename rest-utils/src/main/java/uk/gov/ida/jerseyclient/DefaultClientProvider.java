package uk.gov.ida.jerseyclient;

import io.dropwizard.setup.Environment;
import uk.gov.ida.restclient.ClientProvider;
import uk.gov.ida.restclient.RestfulClientConfiguration;

import javax.inject.Inject;

public class DefaultClientProvider extends ClientProvider {

    @Inject
    public DefaultClientProvider(
            Environment environment,
            RestfulClientConfiguration restfulClientConfiguration) {

        super(
                environment,
                restfulClientConfiguration.getJerseyClientConfiguration(),
                restfulClientConfiguration.getEnableRetryTimeOutConnections(),
                "MicroserviceClient"
        );
    }
}

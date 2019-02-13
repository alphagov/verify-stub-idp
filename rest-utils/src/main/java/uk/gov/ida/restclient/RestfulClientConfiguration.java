package uk.gov.ida.restclient;

import io.dropwizard.client.JerseyClientConfiguration;

public interface RestfulClientConfiguration {

    boolean getEnableRetryTimeOutConnections();

    JerseyClientConfiguration getJerseyClientConfiguration();
}

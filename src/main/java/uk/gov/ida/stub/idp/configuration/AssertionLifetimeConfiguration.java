package uk.gov.ida.stub.idp.configuration;

import io.dropwizard.util.Duration;

public interface AssertionLifetimeConfiguration {
    Duration getAssertionLifetime();
}

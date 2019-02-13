package uk.gov.ida.jerseyclient;

import io.dropwizard.util.Duration;
import uk.gov.ida.configuration.JerseyClientWithRetryBackoffConfiguration;

import java.util.ArrayList;
import java.util.List;

public class JerseyClientWithRetryBackoffHandlerConfigurationBuilder {

    private Duration timeout = Duration.microseconds(500);
    private Duration backOffPeriod = Duration.microseconds(500);
    private Duration connectionTimeout = Duration.microseconds(500);
    private List<String> retryExceptions = new ArrayList<>();
    private int numRetries = 0;
    private boolean chunkedEncodingEnabled = false;

    public static JerseyClientWithRetryBackoffHandlerConfigurationBuilder aJerseyClientWithRetryBackoffHandlerConfiguration() {
        return new JerseyClientWithRetryBackoffHandlerConfigurationBuilder();
    }

    public JerseyClientWithRetryBackoffConfiguration build() {
        return new TestJerseyClientWithRetryBackoffConfiguration(
                1,
                128,
                timeout,
                connectionTimeout,
                Duration.hours(1),
                1024,
                1024,
                numRetries,
                backOffPeriod,
                retryExceptions,
                chunkedEncodingEnabled
        );
    }

    public JerseyClientWithRetryBackoffHandlerConfigurationBuilder withTimeout(Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    public JerseyClientWithRetryBackoffHandlerConfigurationBuilder withRetryBackoffPeriod(Duration backOffPeriod) {
        this.backOffPeriod = backOffPeriod;
        return this;
    }

    public JerseyClientWithRetryBackoffHandlerConfigurationBuilder withRetryExceptionList(List<String> retryExceptions) {
        this.retryExceptions = retryExceptions;
        return this;
    }

    public JerseyClientWithRetryBackoffHandlerConfigurationBuilder withChunkedEncodingEnabled(boolean chunkedEncodingEnabled) {
        this.chunkedEncodingEnabled = chunkedEncodingEnabled;
        return this;
    }

    public JerseyClientWithRetryBackoffHandlerConfigurationBuilder withNumRetries(int numRetries) {
        this.numRetries = numRetries;
        return this;
    }

    public JerseyClientWithRetryBackoffHandlerConfigurationBuilder withConnectionTimeout(Duration connectionTimeout){
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    private static class TestJerseyClientWithRetryBackoffConfiguration extends JerseyClientWithRetryBackoffConfiguration {
        private TestJerseyClientWithRetryBackoffConfiguration(
                int minThreads,
                int maxThreads,
                Duration timeout,
                Duration connectionTimeout,
                Duration timeToLive,
                int maxConnections,
                int maxConnectionsPerRoute,
                int numRetries,
                Duration retryBackoffPeriod,
                List<String> exceptionNames,
                boolean chunkedEncodingEnabled) {

            setMinThreads(minThreads);
            setMaxThreads(maxThreads);
            setTimeout(timeout);
            setConnectionTimeout(connectionTimeout);
            setTimeToLive(timeToLive);
            setMaxConnections(maxConnections);
            setMaxConnectionsPerRoute(maxConnectionsPerRoute);
            setRetryBackoffPeriod(retryBackoffPeriod);
            setRetryExceptionNames(exceptionNames);
            setChunkedEncodingEnabled(chunkedEncodingEnabled);
            setRetries(numRetries);
        }
    }
}

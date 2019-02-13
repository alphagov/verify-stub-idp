package uk.gov.ida.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.util.Duration;

import java.util.List;

public class JerseyClientWithRetryBackoffConfiguration extends JerseyClientConfiguration{

    @JsonProperty
    private Duration retryBackoffPeriod = Duration.seconds(0);

    @JsonProperty
    private List<String> retryExceptionNames = null;

    public Duration getRetryBackoffPeriod() {
        return retryBackoffPeriod;
    }

    public List<String> getRetryExceptionNames() {
        return retryExceptionNames;
    }

    public void setRetryBackoffPeriod(Duration retryBackoffPeriod) {
        this.retryBackoffPeriod = retryBackoffPeriod;
    }

    public void setRetryExceptionNames(List<String> retryExceptionNames){
        this.retryExceptionNames = retryExceptionNames;
    }
}

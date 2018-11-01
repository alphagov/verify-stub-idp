package uk.gov.ida.stub.idp.repositories.reaper;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.Duration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class StaleSessionReaperConfiguration {

    @NotNull
    @Valid
    @JsonProperty
    private Duration sessionIsStaleAfter = Duration.standardHours(4);

    @NotNull
    @Valid
    @JsonProperty
    private Duration reaperFrequency = Duration.standardHours(1);

    @NotNull
    @Valid
    @JsonProperty
    // this is 30 in the upstart script so this gives a bit more time for cleanup before termination
    private Duration terminationTimeout = Duration.standardSeconds(20);

    public StaleSessionReaperConfiguration() {}

    public Duration getSessionIsStaleAfter() {
        return sessionIsStaleAfter;
    }

    public Duration getReaperFrequency() {
        return reaperFrequency;
    }

    public Duration getTerminationTimeout() {
        return terminationTimeout;
    }
}

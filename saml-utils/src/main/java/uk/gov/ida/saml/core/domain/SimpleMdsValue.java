package uk.gov.ida.saml.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import java.io.Serializable;

public class SimpleMdsValue<T> implements Serializable {

    private T value;
    private DateTime from;
    private DateTime to;
    private boolean verified;

    @JsonCreator
    public SimpleMdsValue(@JsonProperty("value") T value, @JsonProperty("from") DateTime from, @JsonProperty("to") DateTime to, @JsonProperty("verified") boolean verified) {
        this.value = value;
        this.from = from;
        this.to = to;
        this.verified = verified;
    }

    public T getValue() {
        return value;
    }

    public DateTime getFrom() {
        return from;
    }

    public DateTime getTo() {
        return to;
    }

    public boolean isVerified() {
        return verified;
    }
}

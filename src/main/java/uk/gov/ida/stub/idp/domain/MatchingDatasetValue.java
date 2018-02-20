package uk.gov.ida.stub.idp.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.base.BaseDateTime;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class MatchingDatasetValue<T> implements Serializable {

    private T value;
    private DateTime from;
    private DateTime to;
    private boolean verified;

    @JsonCreator
    public MatchingDatasetValue(@JsonProperty("value") T value, @JsonProperty("from") DateTime from, @JsonProperty("to") DateTime to, @JsonProperty("verified") boolean verified) {
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

    @Override
    public String toString() {
        return "SimpleMdsValue2{" +
            "value=" + value +
            ", from=" + from +
            ", to=" + to +
            ", verified=" + verified +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchingDatasetValue)) return false;
        MatchingDatasetValue<?> that = (MatchingDatasetValue<?>) o;
        return verified == that.verified &&
            Objects.equals(value, that.value) &&
            Objects.equals(
                Optional.ofNullable(from).map(BaseDateTime::getMillis),
                Optional.ofNullable(that.from).map(BaseDateTime::getMillis)) &&
            Objects.equals(
                Optional.ofNullable(to).map(BaseDateTime::getMillis),
                Optional.ofNullable(that.to).map(BaseDateTime::getMillis));
    }

    @Override
    public int hashCode() {

        return Objects.hash(value, from, to, verified);
    }
}

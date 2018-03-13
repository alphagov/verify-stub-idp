package uk.gov.ida.stub.idp.builders;

import org.joda.time.DateTime;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;

public class MatchingDatasetValueBuilder<T> {

    private T value = null;

    private DateTime from = DateTime.now().minusDays(5);
    private DateTime to = DateTime.now().plusDays(5);
    private boolean verified = false;

    public static <T> MatchingDatasetValueBuilder<T> aSimpleMdsValue() {
        return new MatchingDatasetValueBuilder<>();
    }

    public MatchingDatasetValue<T> build() {
        return new MatchingDatasetValue<>(value, from, to, verified);
    }

    public MatchingDatasetValueBuilder<T> withValue(T value) {
        this.value = value;
        return this;
    }

    public MatchingDatasetValueBuilder<T> withFrom(DateTime from) {
        this.from = from;
        return this;
    }

    public MatchingDatasetValueBuilder<T> withTo(DateTime to) {
        this.to = to;
        return this;
    }

    public MatchingDatasetValueBuilder<T> withVerifiedStatus(boolean verified) {
        this.verified = verified;
        return this;
    }
}

package uk.gov.ida.saml.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class Address implements MdsAttributeValue, Serializable {
    private boolean verified;
    private DateTime from;
    private Optional<DateTime> to = Optional.empty();
    private Optional<String> postCode = Optional.empty();
    private List<String> lines;
    private Optional<String> internationalPostCode = Optional.empty();
    private Optional<String> uprn = Optional.empty();

    public Address(
            List<String> lines,
            String postCode,
            String internationalPostCode,
            String uprn,
            DateTime from,
            DateTime to,
            boolean verified) {

        this.internationalPostCode = Optional.ofNullable(internationalPostCode);
        this.uprn = Optional.ofNullable(uprn);
        this.from = from;
        this.postCode = Optional.ofNullable(postCode);
        this.lines = lines;
        this.to = Optional.ofNullable(to);
        this.verified = verified;
    }

    @JsonCreator
    public Address(
            @JsonProperty("lines") List<String> lines,
            @JsonProperty("postCode") Optional<String> postCode,
            @JsonProperty("internationalPostCode") Optional<String> internationalPostCode,
            @JsonProperty("uprn") Optional<String> uprn,
            @JsonProperty("from") DateTime from,
            @JsonProperty("to") Optional<DateTime> to,
            @JsonProperty("verified") boolean verified) {
        this.lines = lines;
        this.postCode = postCode;
        this.internationalPostCode = internationalPostCode;
        this.uprn = uprn;
        this.from = from;
        this.to = to;
        this.verified = verified;
    }

    public List<String> getLines() {
        return lines;
    }

    public Optional<String> getPostCode() {
        return postCode;
    }

    public Optional<String> getInternationalPostCode() {
        return internationalPostCode;
    }

    public Optional<String> getUPRN() {
        return uprn;
    }

    public DateTime getFrom() {
        return from;
    }

    public Optional<DateTime> getTo() {
        return to;
    }

    public boolean isVerified() {
        return verified;
    }
}

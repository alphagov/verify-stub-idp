package uk.gov.ida.saml.core.domain;

import org.joda.time.DateTime;

public class AssertionRestrictions {
    private String recipient;
    private DateTime notOnOrAfter;
    private String inResponseTo;

    protected AssertionRestrictions() {}

    public AssertionRestrictions(DateTime notOnOrAfter, String inResponseTo, String recipient) {
        this.notOnOrAfter = notOnOrAfter;
        this.inResponseTo = inResponseTo;
        this.recipient = recipient;
    }

    public DateTime getNotOnOrAfter() {
        return notOnOrAfter;
    }

    public String getInResponseTo() {
        return inResponseTo;
    }

    public String getRecipient() {
        return this.recipient;
    }
}

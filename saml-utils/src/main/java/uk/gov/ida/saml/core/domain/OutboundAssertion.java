package uk.gov.ida.saml.core.domain;

import org.joda.time.DateTime;

public class OutboundAssertion {

    private String id;
    private String issuerId;
    private DateTime issueInstant;
    private PersistentId persistentId;
    private AssertionRestrictions assertionRestrictions;

    public OutboundAssertion(
            String id,
            String issuerId,
            DateTime issueInstant,
            PersistentId persistentId,
            AssertionRestrictions assertionRestrictions) {

        this.id = id;
        this.issuerId = issuerId;
        this.issueInstant = issueInstant;
        this.persistentId = persistentId;
        this.assertionRestrictions = assertionRestrictions;
    }

    public PersistentId getPersistentId() {
        return persistentId;
    }

    public AssertionRestrictions getAssertionRestrictions() {
        return assertionRestrictions;
    }

    public String getId() {
        return id;
    }

    public String getIssuerId() {
        return issuerId;
    }

    public DateTime getIssueInstant() {
        return issueInstant;
    }
}

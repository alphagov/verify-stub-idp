package uk.gov.ida.saml.core.domain;

import org.joda.time.DateTime;

import java.util.Optional;

public class HubAssertion extends OutboundAssertion {
    private Optional<Cycle3Dataset> cycle3Data = Optional.empty();

    public HubAssertion(
            String id,
            String issuerId,
            DateTime issueInstant,
            PersistentId persistentId,
            AssertionRestrictions assertionRestrictions,
            Optional<Cycle3Dataset> cycle3Data) {

        super(id, issuerId, issueInstant, persistentId, assertionRestrictions);

        this.cycle3Data = cycle3Data;
    }

    public Optional<Cycle3Dataset> getCycle3Data() {
        return cycle3Data;
    }
}

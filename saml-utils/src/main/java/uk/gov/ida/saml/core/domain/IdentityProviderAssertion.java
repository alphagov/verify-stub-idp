package uk.gov.ida.saml.core.domain;

import org.joda.time.DateTime;

import java.util.Optional;

public class IdentityProviderAssertion extends OutboundAssertion {
    private Optional<MatchingDataset> matchingDataset = Optional.empty();
    private Optional<IdentityProviderAuthnStatement> authnStatement = Optional.empty();

    public IdentityProviderAssertion(
            String id,
            String issuerId,
            DateTime issueInstant,
            PersistentId persistentId,
            AssertionRestrictions assertionRestrictions,
            Optional<MatchingDataset> matchingDataset,
            Optional<IdentityProviderAuthnStatement> authnStatement) {

        super(id, issuerId, issueInstant, persistentId, assertionRestrictions);

        this.matchingDataset = matchingDataset;
        this.authnStatement = authnStatement;
    }

    public Optional<MatchingDataset> getMatchingDataset() {
        return matchingDataset;
    }

    public Optional<IdentityProviderAuthnStatement> getAuthnStatement(){
        return authnStatement;
    }
}

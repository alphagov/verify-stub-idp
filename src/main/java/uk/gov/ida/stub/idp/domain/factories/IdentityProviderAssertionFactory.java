package uk.gov.ida.stub.idp.domain.factories;

import com.google.common.base.Optional;
import org.joda.time.DateTime;
import uk.gov.ida.common.shared.security.IdGenerator;
import uk.gov.ida.saml.core.domain.AssertionRestrictions;
import uk.gov.ida.saml.core.domain.IdentityProviderAssertion;
import uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement;
import uk.gov.ida.saml.core.domain.MatchingDataset;
import uk.gov.ida.saml.core.domain.PersistentId;

import javax.inject.Inject;

import static com.google.common.base.Optional.fromNullable;

public class IdentityProviderAssertionFactory {

    private final IdGenerator idGenerator;

    @Inject
    public IdentityProviderAssertionFactory(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public IdentityProviderAssertion createMatchingDatasetAssertion(
            PersistentId persistentId,
            String issuerId,
            MatchingDataset matchingDataset,
            AssertionRestrictions assertionRestrictions) {

        return new IdentityProviderAssertion(
                idGenerator.getId(),
                issuerId,
                DateTime.now(),
                persistentId,
                assertionRestrictions,
                fromNullable(matchingDataset),
                Optional.absent());
    }

    public IdentityProviderAssertion createAuthnStatementAssertion(
            PersistentId persistentId,
            String issuerId,
            IdentityProviderAuthnStatement idaAuthnStatement,
            AssertionRestrictions assertionRestrictions) {

        return new IdentityProviderAssertion(
                idGenerator.getId(),
                issuerId,
                DateTime.now(),
                persistentId,
                assertionRestrictions,
                Optional.absent(),
                fromNullable(idaAuthnStatement));
    }
}

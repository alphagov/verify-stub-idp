package uk.gov.ida.stub.idp.domain.factories;

import org.joda.time.DateTime;
import uk.gov.ida.saml.core.domain.FraudAuthnDetails;
import uk.gov.ida.saml.core.domain.IdentityProviderAssertion;
import uk.gov.ida.saml.core.domain.IpAddress;
import uk.gov.ida.saml.core.domain.MatchingDataset;
import uk.gov.ida.saml.core.domain.PersistentId;
import uk.gov.ida.stub.idp.domain.IdpUser;

import javax.inject.Inject;

import static uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement.createIdentityProviderFraudAuthnStatement;

public class AssertionFactory {

    private final IdentityProviderAssertionFactory identityProviderAssertionFactory;
    private final AssertionRestrictionsFactory assertionRestrictionsFactory;

    @Inject
    public AssertionFactory(IdentityProviderAssertionFactory identityProviderAssertionFactory, AssertionRestrictionsFactory assertionRestrictionsFactory) {
        this.identityProviderAssertionFactory = identityProviderAssertionFactory;
        this.assertionRestrictionsFactory = assertionRestrictionsFactory;
    }

    private PersistentId createPersistentId(String persistentId) {
        return new PersistentId(persistentId);
    }

    public IdentityProviderAssertion createFraudAuthnStatementAssertion(
            String issuerId,
            IdpUser user,
            String inResponseToId,
            String idpName,
            String indicator,
            IpAddress userIpAddress) {

        return identityProviderAssertionFactory.createAuthnStatementAssertion(
                createPersistentId(user.getPersistentId()),
                issuerId,
                createIdentityProviderFraudAuthnStatement(new FraudAuthnDetails(idpName + DateTime.now().toString() + inResponseToId, indicator), userIpAddress),
                assertionRestrictionsFactory.createRestrictionsForSendingToHub(inResponseToId)
        );
    }

    public IdentityProviderAssertion createMatchingDatasetAssertion(String issuerId, IdpUser user, String requestId) {
        MatchingDataset matchingDataset = MatchingDatasetFactory.create(user);
        return identityProviderAssertionFactory.createMatchingDatasetAssertion(
            createPersistentId(user.getPersistentId()),
            issuerId,
            matchingDataset,
            assertionRestrictionsFactory.createRestrictionsForSendingToHub(requestId));
    }
}

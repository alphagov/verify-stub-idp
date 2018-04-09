package uk.gov.ida.stub.idp.services;

import uk.gov.ida.saml.core.domain.IdentityProviderAssertion;
import uk.gov.ida.saml.core.domain.IpAddress;
import uk.gov.ida.saml.core.domain.PersistentId;
import uk.gov.ida.stub.idp.domain.IdpUser;
import uk.gov.ida.stub.idp.domain.OutboundResponseFromIdp;
import uk.gov.ida.stub.idp.domain.SamlResponse;
import uk.gov.ida.stub.idp.domain.factories.AssertionRestrictionsFactory;
import uk.gov.ida.stub.idp.domain.factories.IdentityProviderAssertionFactory;
import uk.gov.ida.stub.idp.domain.factories.MatchingDatasetFactory;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.repositories.MetadataRepository;
import uk.gov.ida.stub.idp.repositories.Session;
import uk.gov.ida.stub.idp.saml.transformers.OutboundResponseFromIdpTransformerProvider;

import javax.inject.Inject;
import java.net.URI;
import java.util.UUID;

import static uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement.createIdentityProviderAuthnStatement;

public class SuccessAuthnResponseService {

    private final IdentityProviderAssertionFactory identityProviderAssertionFactory;
    private final IdpStubsRepository idpStubsRepository;
    private final MetadataRepository metadataProvider;
    private final AssertionRestrictionsFactory assertionRestrictionsFactory;
    private final OutboundResponseFromIdpTransformerProvider outboundResponseFromIdpTransformerProvider;

    @Inject
    public SuccessAuthnResponseService(
            IdentityProviderAssertionFactory identityProviderAssertionFactory,
            IdpStubsRepository idpStubsRepository,
            MetadataRepository metadataProvider,
            AssertionRestrictionsFactory assertionRestrictionsFactory,
            OutboundResponseFromIdpTransformerProvider outboundResponseFromIdpTransformerProvider) {

        this.identityProviderAssertionFactory = identityProviderAssertionFactory;
        this.idpStubsRepository = idpStubsRepository;
        this.metadataProvider = metadataProvider;
        this.assertionRestrictionsFactory = assertionRestrictionsFactory;
        this.outboundResponseFromIdpTransformerProvider = outboundResponseFromIdpTransformerProvider;
    }

    public SamlResponse getSuccessResponse(boolean randomisePid, String remoteIpAddress, String idpName, Session session) {
        URI hubUrl = metadataProvider.getAssertionConsumerServiceLocation();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);

        IdpUser idpUser = session.getIdpUser().get();
        String requestId = session.getIdaAuthnRequestFromHub().getId();

        PersistentId persistentId = new PersistentId(idpUser.getPersistentId());
        if(randomisePid) {
            persistentId = new PersistentId(UUID.randomUUID().toString());
        }

        IdentityProviderAssertion matchingDatasetAssertion = identityProviderAssertionFactory.createMatchingDatasetAssertion(
                persistentId,
                idp.getIssuerId(),
                MatchingDatasetFactory.create(idpUser),
                assertionRestrictionsFactory.createRestrictionsForSendingToHub(requestId));

        IdentityProviderAssertion authnStatementAssertion = identityProviderAssertionFactory.createAuthnStatementAssertion(
                persistentId,
                idp.getIssuerId(),
                createIdentityProviderAuthnStatement(idpUser.getLevelOfAssurance(), new IpAddress(remoteIpAddress)),
                assertionRestrictionsFactory.createRestrictionsForSendingToHub(requestId));

        OutboundResponseFromIdp idaResponse = OutboundResponseFromIdp.createSuccessResponseFromIdp(
                session.getIdaAuthnRequestFromHub().getId(),
                idp.getIssuerId(),
                matchingDatasetAssertion,
                authnStatementAssertion,
                hubUrl);
        String idpResponse = outboundResponseFromIdpTransformerProvider.get(idp).apply(idaResponse);

        return new SamlResponse(idpResponse, session.getRelayState(), hubUrl);
    }

}

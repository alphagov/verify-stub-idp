package uk.gov.ida.stub.idp.services;

import uk.gov.ida.saml.core.domain.IdentityProviderAssertion;
import uk.gov.ida.saml.core.domain.IpAddress;
import uk.gov.ida.stub.idp.StubIdpModule;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.domain.FraudIndicator;
import uk.gov.ida.stub.idp.domain.OutboundResponseFromIdp;
import uk.gov.ida.stub.idp.domain.SamlResponse;
import uk.gov.ida.stub.idp.domain.factories.AssertionFactory;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.repositories.MetadataRepository;
import uk.gov.ida.stub.idp.repositories.Session;
import uk.gov.ida.stub.idp.saml.transformers.OutboundResponseFromIdpTransformerProvider;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;

import static uk.gov.ida.stub.idp.domain.OutboundResponseFromIdp.createAuthnCancelResponseIssuedByIdp;
import static uk.gov.ida.stub.idp.domain.OutboundResponseFromIdp.createAuthnFailedResponseIssuedByIdp;
import static uk.gov.ida.stub.idp.domain.OutboundResponseFromIdp.createAuthnPendingResponseIssuedByIdp;
import static uk.gov.ida.stub.idp.domain.OutboundResponseFromIdp.createNoAuthnContextResponseIssuedByIdp;
import static uk.gov.ida.stub.idp.domain.OutboundResponseFromIdp.createRequesterErrorResponseIssuedByIdp;
import static uk.gov.ida.stub.idp.domain.OutboundResponseFromIdp.createSuccessResponseFromIdp;
import static uk.gov.ida.stub.idp.domain.OutboundResponseFromIdp.createUpliftFailedResponseIssuedByIdp;

public class NonSuccessAuthnResponseService {

    private final IdpStubsRepository idpStubsRepository;
    private final MetadataRepository metadataRepository;
    private final AssertionFactory assertionFactory;
    private final OutboundResponseFromIdpTransformerProvider outboundResponseFromIdpTransformerProvider;

    @Inject
    public NonSuccessAuthnResponseService(
            IdpStubsRepository idpStubsRepository,
            @Named(StubIdpModule.HUB_METADATA_REPOSITORY) MetadataRepository metadataRepository,
            AssertionFactory assertionFactory,
            OutboundResponseFromIdpTransformerProvider outboundResponseFromIdpTransformerProvider) {

        this.idpStubsRepository = idpStubsRepository;
        this.metadataRepository = metadataRepository;
        this.assertionFactory = assertionFactory;
        this.outboundResponseFromIdpTransformerProvider = outboundResponseFromIdpTransformerProvider;
    }

    public SamlResponse generateFraudResponse(String idpName, String samlRequestId, FraudIndicator fraudIndicatorParam, String clientIpAddress, Session session) {
        String requestId = session.getIdaAuthnRequestFromHub().getId();
        DatabaseIdpUser idpUser = IdpUserService.createRandomUser();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        URI hubUrl = metadataRepository.getAssertionConsumerServiceLocation();

        IdentityProviderAssertion matchingDatasetAssertion = assertionFactory.createMatchingDatasetAssertion(idp.getIssuerId(), idpUser, requestId);
        IdentityProviderAssertion authnStatementAssertion = assertionFactory.createFraudAuthnStatementAssertion(
                idp.getIssuerId(),
                idpUser,
                requestId,
                idpName,
                fraudIndicatorParam.name(),
                new IpAddress(clientIpAddress));

        OutboundResponseFromIdp successResponseFromIdp = createSuccessResponseFromIdp(
                samlRequestId,
                idp.getIssuerId(),
                matchingDatasetAssertion,
                authnStatementAssertion,
                hubUrl);

        return generateResponse(idp, successResponseFromIdp, hubUrl, session.getRelayState());
    }

    public SamlResponse generateAuthnPending(String idpName, String samlRequestId, String relayState) {
        URI hubUrl = metadataRepository.getAssertionConsumerServiceLocation();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        OutboundResponseFromIdp authnPendingResponseIssuedByIdp = createAuthnPendingResponseIssuedByIdp(samlRequestId, idp.getIssuerId(), hubUrl);
        return generateResponse(idp, authnPendingResponseIssuedByIdp, hubUrl, relayState);
    }

    public SamlResponse generateUpliftFailed(String idpName, String samlRequestId, String relayState) {
        URI hubUrl = metadataRepository.getAssertionConsumerServiceLocation();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        OutboundResponseFromIdp upliftFailedResponseIssuedByIdp = createUpliftFailedResponseIssuedByIdp(samlRequestId, idp.getIssuerId(), hubUrl);
        return generateResponse(idp, upliftFailedResponseIssuedByIdp, hubUrl, relayState);
    }

    public SamlResponse generateAuthnCancel(String idpName, String samlRequestId, String relayState) {
        URI hubUrl = metadataRepository.getAssertionConsumerServiceLocation();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        OutboundResponseFromIdp authnCancelResponseIssuedByIdp = createAuthnCancelResponseIssuedByIdp(samlRequestId, idp.getIssuerId(), hubUrl);
        return generateResponse(idp, authnCancelResponseIssuedByIdp, hubUrl, relayState);
    }

    public SamlResponse generateNoAuthnContext(String idpName, String samlRequestId, String relayState) {
        URI hubUrl = metadataRepository.getAssertionConsumerServiceLocation();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        OutboundResponseFromIdp noAuthnContextResponseIssuedByIdp = createNoAuthnContextResponseIssuedByIdp(samlRequestId, idp.getIssuerId(), hubUrl);
        return generateResponse(idp, noAuthnContextResponseIssuedByIdp, hubUrl, relayState);
    }

    public SamlResponse generateAuthnFailed(String idpName, String samlRequestId, String relayState) {
        URI hubUrl = metadataRepository.getAssertionConsumerServiceLocation();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        OutboundResponseFromIdp failureResponse = createAuthnFailedResponseIssuedByIdp(samlRequestId, idp.getIssuerId(), hubUrl);
        return generateResponse(idp, failureResponse, hubUrl, relayState);
    }

    public SamlResponse generateRequesterError(String samlRequestId, String requesterErrorMessage, String idpName, String relayState) {
        URI hubUrl = metadataRepository.getAssertionConsumerServiceLocation();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        OutboundResponseFromIdp requesterErrorResponse = createRequesterErrorResponseIssuedByIdp(
                samlRequestId,
                idp.getIssuerId(),
                hubUrl,
                requesterErrorMessage);
        return generateResponse(idp, requesterErrorResponse, hubUrl, relayState);
    }

    private SamlResponse generateResponse(Idp idp, OutboundResponseFromIdp outboundResponseFromIdp, URI hubUrl, String relayState) {
        String samlResponse = outboundResponseFromIdpTransformerProvider.get(idp).apply(outboundResponseFromIdp);
        return new SamlResponse(samlResponse, relayState, hubUrl);
    }

}

package uk.gov.ida.stub.idp.services;

import com.google.common.collect.ImmutableList;
import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.hub.domain.IdaAuthnRequestFromHub;
import uk.gov.ida.stub.idp.domain.FraudIndicator;
import uk.gov.ida.stub.idp.domain.IdpIdaStatus;
import uk.gov.ida.stub.idp.domain.OutboundResponseFromIdp;
import uk.gov.ida.stub.idp.domain.factories.AssertionFactory;
import uk.gov.ida.stub.idp.repositories.*;
import uk.gov.ida.stub.idp.saml.transformers.OutboundResponseFromIdpTransformerProvider;

import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ida.saml.core.domain.AuthnContext.LEVEL_2;

@RunWith(MockitoJUnitRunner.class)
public class NonSuccessAuthnResponseServiceTest {

    private final String IDP_NAME = "an idp name";
    private final String REQUEST_ID = "requestId";
    private final URI HUB_URI = URI.create("hub");
    private final String IDP_ISSUER = "idpissuer";
    private final String RELAY_STATE = "relayrelayrelay";

    @BeforeClass
    public static void doALittleHackToMakeGuicierHappyForSomeReason() {
        JerseyGuiceUtils.reset();
    }

    private NonSuccessAuthnResponseService nonSuccessAuthnResponseService;

    @Mock
    private IdpStubsRepository idpStubsRepository;
    @Mock
    private MetadataRepository metadataRepository;
    @Mock
    private AssertionFactory assertionFactory;
    @Mock
    private OutboundResponseFromIdpTransformerProvider outboundResponseFromIdpTransformerProvider;
    @Mock
    private Idp idp;
    @Mock
    private Function<OutboundResponseFromIdp, String> transformer;

    @Captor
    private ArgumentCaptor<OutboundResponseFromIdp> captor;

    @Before
    public void createResource() {
        nonSuccessAuthnResponseService = new NonSuccessAuthnResponseService(
                idpStubsRepository,
                metadataRepository,
                assertionFactory,
                outboundResponseFromIdpTransformerProvider);
        when(idpStubsRepository.getIdpWithFriendlyId(IDP_NAME)).thenReturn(idp);
        when(idp.getIssuerId()).thenReturn(IDP_ISSUER);
        when(metadataRepository.getAssertionConsumerServiceLocation()).thenReturn(HUB_URI);
        when(outboundResponseFromIdpTransformerProvider.get(idp)).thenReturn(transformer);
    }

    @Test
    public void shouldBuildAuthnPending(){
        nonSuccessAuthnResponseService.generateAuthnPending(IDP_NAME, REQUEST_ID, RELAY_STATE).getResponseString();

        verify(transformer).apply(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(IdpIdaStatus.authenticationPending());
    }

    @Test
    public void shouldBuildNoAuthnContext(){
        nonSuccessAuthnResponseService.generateNoAuthnContext(IDP_NAME, REQUEST_ID, RELAY_STATE).getResponseString();

        verify(transformer).apply(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(IdpIdaStatus.noAuthenticationContext());
    }

    @Test
    public void shouldBuildUpliftFailed(){
        nonSuccessAuthnResponseService.generateUpliftFailed(IDP_NAME, REQUEST_ID, RELAY_STATE).getResponseString();

        verify(transformer).apply(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(IdpIdaStatus.upliftFailed());
    }

    @Test
    public void shouldBuildNoAuthnCancel(){
        nonSuccessAuthnResponseService.generateAuthnCancel(IDP_NAME, REQUEST_ID, RELAY_STATE).getResponseString();

        verify(transformer).apply(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(IdpIdaStatus.authenticationCancelled());
    }

    @Test
    public void shouldBuildNoAuthnFailed(){
        nonSuccessAuthnResponseService.generateAuthnFailed(IDP_NAME, REQUEST_ID, RELAY_STATE).getResponseString();

        verify(transformer).apply(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(IdpIdaStatus.authenticationFailed());
    }

    @Test
    public void shouldBuildRequesterError(){
        nonSuccessAuthnResponseService.generateRequesterError(REQUEST_ID, "error", IDP_NAME, RELAY_STATE).getResponseString();

        verify(transformer).apply(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(IdpIdaStatus.requesterError(Optional.of("error")));
    }

    @Test
    public void shouldBuildFraudResponse(){
        IdpSession session = new IdpSession(SessionId.createNewSessionId(),
                IdaAuthnRequestFromHub.createRequestReceivedFromHub(REQUEST_ID, HUB_URI.toString(), ImmutableList.of(LEVEL_2), false, DateTime.now(), AuthnContextComparisonTypeEnumeration.EXACT),
                RELAY_STATE, null, null, null, null, null);
        nonSuccessAuthnResponseService.generateFraudResponse(IDP_NAME, REQUEST_ID, FraudIndicator.FI01, "ipAddress", session).getResponseString();

        verify(transformer).apply(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(IdpIdaStatus.success());
    }
}

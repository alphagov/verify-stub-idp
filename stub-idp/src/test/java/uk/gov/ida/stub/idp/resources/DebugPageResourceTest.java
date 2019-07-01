package uk.gov.ida.stub.idp.resources;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.hub.domain.IdaAuthnRequestFromHub;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.resources.idp.DebugPageResource;
import uk.gov.ida.stub.idp.views.DebugPageView;

import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DebugPageResourceTest {

    private final String IDP_NAME = "an idp name";
    private final SessionId SESSION_ID = SessionId.createNewSessionId();
    private final String RELAY_STATE = "relayState";

    @BeforeClass
    public static void doALittleHackToMakeGuicierHappyForSomeReason() {
        JerseyGuiceUtils.reset();
    }

    private DebugPageResource resource;
    private Idp idp = new Idp(IDP_NAME,IDP_NAME,"an assetId", false, null, null);

    @Mock
    private IdpStubsRepository idpStubsRepository;
    @Mock
    private SessionRepository<IdpSession> sessionRepository;
    @Mock
    private IdaAuthnRequestFromHub idaAuthnRequestFromHub;


    @Before
    public void createResource() {
        resource = new DebugPageResource(
                idpStubsRepository,
                sessionRepository
        );
        when(idpStubsRepository.getIdpWithFriendlyId(IDP_NAME)).thenReturn(idp);
    }

    @Test
    public void shouldHaveNullJourneyIdInPageViewWhenNoIdReceived() {
        when(sessionRepository.get(SESSION_ID)).thenReturn(Optional.ofNullable(new IdpSession(SESSION_ID, idaAuthnRequestFromHub, RELAY_STATE, null, null, null, null, Optional.empty())));

        Response response = resource.get(IDP_NAME, SESSION_ID);

        assertThat(response.getEntity()).isInstanceOf(DebugPageView.class);
        assertThat(((DebugPageView)response.getEntity()).getSingleIdpJourneyId()).isNull();
    }

    @Test
    public void shouldHaveJourneyIdInPageViewWhenIdReceived() {
        UUID uuid = UUID.randomUUID();
        when(sessionRepository.get(SESSION_ID)).thenReturn(Optional.ofNullable(new IdpSession(SESSION_ID, idaAuthnRequestFromHub, RELAY_STATE, null, null, null, null, Optional.ofNullable(uuid))));

        Response response = resource.get(IDP_NAME, SESSION_ID);

        assertThat(response.getEntity()).isInstanceOf(DebugPageView.class);
        assertThat(((DebugPageView)response.getEntity()).getSingleIdpJourneyId()).isEqualTo(uuid);
    }

}

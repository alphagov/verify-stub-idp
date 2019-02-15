package uk.gov.ida.stub.idp.resources;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.hub.domain.IdaAuthnRequestFromHub;
import uk.gov.ida.stub.idp.cookies.CookieFactory;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.domain.SamlResponseFromValue;
import uk.gov.ida.stub.idp.domain.SubmitButtonValue;
import uk.gov.ida.stub.idp.exceptions.InvalidSessionIdException;
import uk.gov.ida.stub.idp.exceptions.InvalidUsernameOrPasswordException;
import uk.gov.ida.stub.idp.repositories.AllIdpsUserRepository;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.IdpSessionRepository;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.resources.idp.LoginPageResource;
import uk.gov.ida.stub.idp.services.IdpUserService;
import uk.gov.ida.stub.idp.services.NonSuccessAuthnResponseService;
import uk.gov.ida.stub.idp.views.ErrorMessageType;
import uk.gov.ida.stub.idp.views.SamlResponseRedirectViewFactory;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginPageResourceTest {

    private final String SAML_REQUEST_ID = "samlRequestId";
    private final String IDP_NAME = "an idp name";
    private final SessionId SESSION_ID = SessionId.createNewSessionId();
    private final String RELAY_STATE = "relayState";
    private final String USERNAME = "username";
    private final String PASSWORD = "password";

    @BeforeClass
    public static void doALittleHackToMakeGuicierHappyForSomeReason() {
        JerseyGuiceUtils.reset();
    }
    
    private LoginPageResource resource;

    @Mock
    private IdpStubsRepository idpStubsRepository;
    @Mock
    private IdpSessionRepository sessionRepository;
    @Mock
    private NonSuccessAuthnResponseService nonSuccessAuthnResponseService;
    @Mock
    IdaAuthnRequestFromHub idaAuthnRequestFromHub;
    @Mock
    private IdpUserService idpUserService;
    @Mock
    private AllIdpsUserRepository allIdpsUserRepository;
    @Mock
    private DatabaseIdpUser databaseIdpUser;
    @Mock
    private Idp idp;
    @Mock
    private CookieFactory cookieFactory;

    @Before
    public void createResource() {
        resource = new LoginPageResource(
                idpStubsRepository,
                nonSuccessAuthnResponseService,
                new SamlResponseRedirectViewFactory(),
                idpUserService,
                sessionRepository,
                cookieFactory);

        when(sessionRepository.get(SESSION_ID)).thenReturn(Optional.ofNullable(new IdpSession(SESSION_ID, idaAuthnRequestFromHub, RELAY_STATE, null, null, null, null, null)));
        when(sessionRepository.deleteAndGet(SESSION_ID)).thenReturn(Optional.ofNullable(new IdpSession(SESSION_ID, idaAuthnRequestFromHub, RELAY_STATE, null, null, null, null, null)));
        when(idaAuthnRequestFromHub.getId()).thenReturn(SAML_REQUEST_ID);
    }

    @Test
    public void shouldBuildNoAuthnContext(){
        when(nonSuccessAuthnResponseService.generateNoAuthnContext(anyString(), anyString(), eq(RELAY_STATE))).thenReturn(new SamlResponseFromValue<String>("saml", Function.identity(), RELAY_STATE, URI.create("uri")));

        resource.postNoAuthnContext(IDP_NAME, SESSION_ID);

        verify(nonSuccessAuthnResponseService).generateNoAuthnContext(IDP_NAME, SAML_REQUEST_ID, RELAY_STATE);
    }

    @Test
    public void shouldBuildUpliftFailed(){
        when(nonSuccessAuthnResponseService.generateUpliftFailed(anyString(), anyString(), eq(RELAY_STATE))).thenReturn(new SamlResponseFromValue<String>("saml", Function.identity(), RELAY_STATE, URI.create("uri")));

        resource.postUpliftFailed(IDP_NAME, SESSION_ID);

        verify(nonSuccessAuthnResponseService).generateUpliftFailed(IDP_NAME, SAML_REQUEST_ID, RELAY_STATE);
    }

    @Test
    public void shouldBuildNoAuthnCancel(){
        when(nonSuccessAuthnResponseService.generateAuthnCancel(anyString(), anyString(), eq(RELAY_STATE))).thenReturn(new SamlResponseFromValue<String>("saml", Function.identity(), RELAY_STATE, URI.create("uri")));

        resource.post(IDP_NAME, USERNAME, PASSWORD, SubmitButtonValue.Cancel, SESSION_ID);

        verify(nonSuccessAuthnResponseService).generateAuthnCancel(IDP_NAME, SAML_REQUEST_ID, RELAY_STATE);
    }

    @Test
    public void shouldBuildSuccessResponse() throws InvalidUsernameOrPasswordException, InvalidSessionIdException {

        when(allIdpsUserRepository.getUserForIdp(anyString(), anyString())).thenReturn(Optional.of(databaseIdpUser));

        final Response response = resource.post(IDP_NAME, USERNAME, PASSWORD, SubmitButtonValue.SignIn, SESSION_ID);

        verify(idpUserService).attachIdpUserToSession(IDP_NAME, USERNAME, PASSWORD, SESSION_ID);
        assertThat(response.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
    }

    @Test
    public void shouldBuildAuthnPending(){
        when(nonSuccessAuthnResponseService.generateAuthnPending(anyString(), anyString(), eq(RELAY_STATE))).thenReturn(new SamlResponseFromValue<String>("saml", Function.identity(), RELAY_STATE, URI.create("uri")));

        resource.postAuthnPending(IDP_NAME, SESSION_ID);

        verify(nonSuccessAuthnResponseService).generateAuthnPending(IDP_NAME, SAML_REQUEST_ID, RELAY_STATE);
    }

    @Test
    public void shouldRedirectToConsentWhenNewlyRegisteredUserReturnsFromHub() {
        Optional<IdpSession> idpSession = Optional.of(Mockito.mock(IdpSession.class));
        when(idpStubsRepository.getIdpWithFriendlyId(IDP_NAME)).thenReturn(idp);
        when(sessionRepository.get(SESSION_ID)).thenReturn(idpSession);
        when(idpSession.get().getIdaAuthnRequestFromHub()).thenReturn(Mockito.mock(IdaAuthnRequestFromHub.class));
        when(idpSession.get().getIdpUser()).thenReturn(Optional.of(databaseIdpUser));
        final Response response = resource.get(IDP_NAME, Optional.of(ErrorMessageType.NO_ERROR), SESSION_ID);
        assertThat(response.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
        assertThat(response.getLocation().toString()).contains("consent");
    }

    @Test
    public void shouldRedirectLoggedInUserToHomePageIfNoIdaAuthReqFromHub() {
        Optional<IdpSession> idpSession = Optional.of(Mockito.mock(IdpSession.class));
        when(idpStubsRepository.getIdpWithFriendlyId(IDP_NAME)).thenReturn(idp);
        when(sessionRepository.get(SESSION_ID)).thenReturn(idpSession);
        when(idpSession.get().getIdaAuthnRequestFromHub()).thenReturn(null);
        when(idpSession.get().getIdpUser()).thenReturn(Optional.of(databaseIdpUser));
        final Response response = resource.get(IDP_NAME, Optional.of(ErrorMessageType.NO_ERROR), SESSION_ID);
        assertThat(response.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
        assertThat(response.getLocation().toString()).contains("an%20idp%20name");
    }

    @Test
    public void shouldShowLoginFormWhenNoCookiePresent() {
        when(idpStubsRepository.getIdpWithFriendlyId(IDP_NAME)).thenReturn(idp);
        final Response response = resource.get(IDP_NAME, Optional.of(ErrorMessageType.NO_ERROR), null);
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void shouldPresentLoginScreenInWhenThereIsNoActivePreRegSession() {
        Optional<IdpSession> preRegSession = Optional.of(Mockito.mock(IdpSession.class));
        when(sessionRepository.get(SESSION_ID)).thenReturn(preRegSession);
        when(preRegSession.get().getIdpUser()).thenReturn(Optional.empty());
        when(idpStubsRepository.getIdpWithFriendlyId(IDP_NAME)).thenReturn(idp);
        when(idp.getDisplayName()).thenReturn("mock idp display name");
        when(idp.getFriendlyId()).thenReturn("mock idp friendly id");
        when(idp.getAssetId()).thenReturn("mock idp asset id");
        final Response response = resource.get(IDP_NAME, Optional.of(ErrorMessageType.NO_ERROR),SESSION_ID);
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void shouldLogUserInAndTakeToHomePageWhenNoIdaReq() {
        Optional<IdpSession> idpSession = Optional.of(Mockito.mock(IdpSession.class));
        when(idpSession.get().getIdaAuthnRequestFromHub()).thenReturn(null);
        when(idpSession.get().getIdpUser()).thenReturn(Optional.empty());
        final Response response = resource.post(IDP_NAME,USERNAME,PASSWORD, SubmitButtonValue.SignIn, SESSION_ID);
        assertThat(response.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
        assertThat(response.getLocation().toString()).contains("an%20idp%20name");
    }

    @Test
    public void shouldLogUserInAndTakeToConsentPageWhenIdaReqPresent() {
        Optional<IdpSession> idpSession = Optional.of(Mockito.mock(IdpSession.class));
        when(idpSession.get().getIdaAuthnRequestFromHub()).thenReturn(idaAuthnRequestFromHub);
        when(idpSession.get().getIdpUser()).thenReturn(Optional.empty());
        final Response response = resource.post(IDP_NAME,USERNAME,PASSWORD, SubmitButtonValue.SignIn, SESSION_ID);
        assertThat(response.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
        assertThat(response.getLocation().toString()).contains("consent");
    }
}

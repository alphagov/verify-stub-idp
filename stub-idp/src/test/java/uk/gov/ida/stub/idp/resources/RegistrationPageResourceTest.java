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
import uk.gov.ida.stub.idp.cookies.CookieFactory;
import uk.gov.ida.stub.idp.domain.SamlResponseFromValue;
import uk.gov.ida.stub.idp.exceptions.IncompleteRegistrationException;
import uk.gov.ida.stub.idp.exceptions.InvalidDateException;
import uk.gov.ida.stub.idp.exceptions.InvalidSessionIdException;
import uk.gov.ida.stub.idp.exceptions.InvalidUsernameOrPasswordException;
import uk.gov.ida.stub.idp.exceptions.UsernameAlreadyTakenException;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.IdpSessionRepository;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.resources.idp.RegistrationPageResource;
import uk.gov.ida.stub.idp.services.IdpUserService;
import uk.gov.ida.stub.idp.services.NonSuccessAuthnResponseService;
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
import static uk.gov.ida.saml.core.domain.AuthnContext.LEVEL_2;
import static uk.gov.ida.stub.idp.domain.SubmitButtonValue.Cancel;
import static uk.gov.ida.stub.idp.domain.SubmitButtonValue.Register;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationPageResourceTest {

    private final String IDP_NAME = "an idp name";
    private final SessionId SESSION_ID = SessionId.createNewSessionId();
    private final String RELAY_STATE = "relayState";
    private final String SAML_REQUEST_ID = "samlRequestId";

    @BeforeClass
    public static void doALittleHackToMakeGuicierHappyForSomeReason() {
        JerseyGuiceUtils.reset();
    }

    private RegistrationPageResource resource;

    @Mock
    private IdpStubsRepository idpStubsRepository;
    @Mock
    private IdpUserService idpUserService;
    @Mock
    private NonSuccessAuthnResponseService nonSuccessAuthnResponseService;
    @Mock
    private SessionRepository<IdpSession> sessionRepository;
    @Mock
    private IdaAuthnRequestFromHub idaAuthnRequestFromHub;
    @Mock
    private CookieFactory cookieFactory;
    @Mock
    IdpSessionRepository idpSessionRepository;
    @Mock
    IdpSession idpSession;

    @Before
    public void createResource() {
        resource = new RegistrationPageResource(
                idpStubsRepository,
                idpUserService,
                new SamlResponseRedirectViewFactory(),
                nonSuccessAuthnResponseService,
                sessionRepository,
                cookieFactory,
                idpSessionRepository);

        when(sessionRepository.get(SESSION_ID)).thenReturn(Optional.ofNullable(new IdpSession(SESSION_ID, idaAuthnRequestFromHub, RELAY_STATE, null, null, null, null, null)));
        when(sessionRepository.deleteAndGet(SESSION_ID)).thenReturn(Optional.ofNullable(new IdpSession(SESSION_ID, idaAuthnRequestFromHub, RELAY_STATE, null, null, null, null, null)));
        when(idaAuthnRequestFromHub.getId()).thenReturn(SAML_REQUEST_ID);
    }

    @Test
    public void shouldHaveStatusAuthnCancelledResponseWhenUserCancels(){
        when(nonSuccessAuthnResponseService.generateAuthnCancel(anyString(), anyString(), eq(RELAY_STATE))).thenReturn(new SamlResponseFromValue<String>("saml", Function.identity(), RELAY_STATE, URI.create("uri")));

        resource.post(IDP_NAME, null, null, null, null, null, null, null, null, null, null, Cancel, SESSION_ID);

        verify(nonSuccessAuthnResponseService).generateAuthnCancel(IDP_NAME, SAML_REQUEST_ID, RELAY_STATE);
    }

    @Test
    public void shouldHaveResponseStatusRedirectWhenUserRegisters() throws InvalidSessionIdException, IncompleteRegistrationException, InvalidDateException, UsernameAlreadyTakenException, InvalidUsernameOrPasswordException {

        when(sessionRepository.get(SESSION_ID)).thenReturn(Optional.of(idpSession));
        when(idpSession.getIdaAuthnRequestFromHub()).thenReturn(idaAuthnRequestFromHub);
        final Response response = resource.post(IDP_NAME, "bob", "jones", "address line 1", "address line 2", "address town", "address postcode", "2000-01-01", "username", "password", LEVEL_2, Register, SESSION_ID);

        assertThat(response.getStatus()).isEqualTo(303);
        assertThat(response.getLocation().toString()).contains("consent");
        verify(idpUserService).createAndAttachIdpUserToSession(eq(IDP_NAME), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), eq(LEVEL_2), anyString(), anyString(), anyString(), eq(SESSION_ID));
    }

    @Test
    public void shouldHaveResponseStatusRedirectWhenUserPreRegisters() throws InvalidSessionIdException, IncompleteRegistrationException, InvalidDateException, UsernameAlreadyTakenException, InvalidUsernameOrPasswordException {

        when(sessionRepository.get(SESSION_ID)).thenReturn(Optional.of(idpSession));
        when(idpSession.getIdaAuthnRequestFromHub()).thenReturn(null);
        final Response response = resource.post(IDP_NAME, "bob", "jones", "address line 1", "address line 2", "address town", "address postcode", "2000-01-01", "username", "password", LEVEL_2, Register, SESSION_ID);

        assertThat(response.getStatus()).isEqualTo(303);
        assertThat(response.getLocation().toString()).contains("start-prompt");
        verify(idpUserService).createAndAttachIdpUserToSession(eq(IDP_NAME), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), eq(LEVEL_2), anyString(), anyString(), anyString(), eq(SESSION_ID));
    }

}

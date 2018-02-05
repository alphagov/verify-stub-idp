package uk.gov.ida.stub.idp.resources;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.domain.EidasAuthnRequest;
import uk.gov.ida.stub.idp.repositories.Session;
import uk.gov.ida.stub.idp.repositories.SessionRepository;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EidasLoginPageResourceTest {

    @BeforeClass
    public static void doALittleHackToMakeGuicierHappyForSomeReason() {
        JerseyGuiceUtils.reset();
    }

    private EidasLoginPageResource resource;

    private final String SCHEME_NAME = "schemeName";
    private final SessionId SESSION_ID = SessionId.createNewSessionId();
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private Session session;

    @Mock
    private SessionRepository sessionRepository;

    @Before
    public void setUp(){
        resource = new EidasLoginPageResource(sessionRepository);
        EidasAuthnRequest eidasAuthnRequest = new EidasAuthnRequest("request-id", "issuer", "destination", "loa", Collections.emptyList());
        session = new Session(null, eidasAuthnRequest, null, null, null, Optional.empty(), Optional.empty());
        when(sessionRepository.get(SESSION_ID)).thenReturn(Optional.ofNullable(session));
    }

    @Test
    public void loginShouldRedirectToEidasConsentResource(){
        final Response response = resource.post(SCHEME_NAME, USERNAME, PASSWORD, SESSION_ID);

        assertThat(session.getEidasUser().isPresent()).isTrue();
        assertThat(response.getLocation()).isEqualTo(UriBuilder.fromPath(Urls.EIDAS_CONSENT_RESOURCE)
                .build(SCHEME_NAME));
        assertThat(response.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
    }

    @Test
    public void loginShouldReturnASuccessfulResponse(){
        final Response response = resource.get(SCHEME_NAME, Optional.empty(), SESSION_ID);

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test(expected = WebApplicationException.class)
    public void loginShouldThrowAWebApplicationExceptionWhenSessionIsEmpty(){
        resource.get(SCHEME_NAME, Optional.empty(), null);
    }
}

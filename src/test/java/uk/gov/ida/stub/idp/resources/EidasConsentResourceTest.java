package uk.gov.ida.stub.idp.resources;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.domain.EidasAuthnRequest;
import uk.gov.ida.stub.idp.domain.EidasUser;
import uk.gov.ida.stub.idp.domain.SamlResponseFromValue;
import uk.gov.ida.stub.idp.repositories.Session;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.services.EidasSuccessAuthnResponseService;
import uk.gov.ida.stub.idp.views.SamlResponseRedirectViewFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.util.Collections;
import java.util.Optional;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EidasConsentResourceTest {

    @BeforeClass
    public static void doALittleHackToMakeGuicierHappyForSomeReason() {
        JerseyGuiceUtils.reset();
    }

    private EidasConsentResource resource;

    private final String SCHEME_NAME = "schemeName";
    private final SessionId SESSION_ID = SessionId.createNewSessionId();
    private Session session;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private EidasSuccessAuthnResponseService successAuthnResponseService;

    @Mock
    private SamlResponseRedirectViewFactory samlResponseRedirectViewFactory;

    @Before
    public void setUp(){
        resource = new EidasConsentResource(sessionRepository, successAuthnResponseService, samlResponseRedirectViewFactory);

        EidasAuthnRequest eidasAuthnRequest = new EidasAuthnRequest("request-id", "issuer", "destination", "loa", Collections.emptyList());
        session = new Session(SESSION_ID, eidasAuthnRequest, null, null, null, null, null);
        EidasUser user = new EidasUser("Jane", "Doe", "pid", new LocalDate(1990, 1, 2), null, null);
        session.setEidasUser(user);
        when(sessionRepository.get(SESSION_ID)).thenReturn(Optional.of(session));
        when(sessionRepository.deleteAndGet(SESSION_ID)).thenReturn(Optional.of(session));
    }

    @Test
    public void getShouldReturnASuccessfulResponseWhenSessionIsValid(){
        final Response response = resource.get(SCHEME_NAME, SESSION_ID);

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void postShouldReturnASuccessfulResponseWhenSessionIsValid() {
        SamlResponseFromValue<org.opensaml.saml.saml2.core.Response> samlResponse = new SamlResponseFromValue<org.opensaml.saml.saml2.core.Response>(null, (r) -> null, null, null);
        when(successAuthnResponseService.getEidasSuccessResponse(session, SCHEME_NAME)).thenReturn(samlResponse);
        when(samlResponseRedirectViewFactory.sendSamlMessage(samlResponse)).thenReturn(Response.ok().build());

        final Response response = resource.consent(SCHEME_NAME, "submit", SESSION_ID);

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test(expected = WebApplicationException.class)
    public void shouldThrowAWebApplicationExceptionWhenSessionIsEmpty(){
        resource.get(SCHEME_NAME, null);
    }

}

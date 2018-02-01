package uk.gov.ida.stub.idp.resources;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.notification.saml.translation.EidasAuthnRequest;
import uk.gov.ida.stub.idp.domain.SamlResponse;
import uk.gov.ida.stub.idp.repositories.Session;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.services.SuccessAuthnResponseService;
import uk.gov.ida.stub.idp.views.SamlResponseRedirectViewFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

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

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private SuccessAuthnResponseService successAuthnResponseService;

    @Mock
    private SamlResponseRedirectViewFactory samlResponseRedirectViewFactory;

    @Before
    public void setUp(){
        resource = new EidasConsentResource(sessionRepository, successAuthnResponseService, samlResponseRedirectViewFactory);

        when(sessionRepository.get(SESSION_ID)).thenReturn(Optional.ofNullable(new Session(null, (EidasAuthnRequest)null, null, null, null, Optional.empty(), Optional.empty())));
    }

    @Test
    public void getShouldReturnASuccessfulResponseWhenSessionIsValid(){
        final Response response = resource.get(SCHEME_NAME, SESSION_ID);

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void postShouldReturnASuccessfulResponseWhenSessionIsValid(){
        Session session = new Session(SESSION_ID, (EidasAuthnRequest) null, null, null, null, null, null);
        when(sessionRepository.deleteAndGet(SESSION_ID)).thenReturn(Optional.ofNullable(session));

        SamlResponse samlResponse = new SamlResponse(null, null, null);
        when(successAuthnResponseService.getEidasSuccessResponse(session)).thenReturn(samlResponse);
        when(samlResponseRedirectViewFactory.sendSamlMessage(samlResponse)).thenReturn(Response.ok().build());

        final Response response = resource.consent(SCHEME_NAME, null, true, SESSION_ID );

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test(expected = WebApplicationException.class)
    public void shouldThrowAWebApplicationExceptionWhenSessionIsEmpty(){
        resource.get(SCHEME_NAME, null);
    }

}

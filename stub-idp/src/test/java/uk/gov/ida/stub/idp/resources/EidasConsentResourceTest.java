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
import uk.gov.ida.stub.idp.domain.EidasScheme;
import uk.gov.ida.stub.idp.domain.EidasUser;
import uk.gov.ida.stub.idp.domain.SamlResponseFromValue;
import uk.gov.ida.stub.idp.exceptions.GenericStubIdpException;
import uk.gov.ida.stub.idp.exceptions.InvalidSigningAlgorithmException;
import uk.gov.ida.stub.idp.repositories.EidasSession;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.repositories.StubCountry;
import uk.gov.ida.stub.idp.repositories.StubCountryRepository;
import uk.gov.ida.stub.idp.resources.eidas.EidasConsentResource;
import uk.gov.ida.stub.idp.services.EidasAuthnResponseService;
import uk.gov.ida.stub.idp.views.SamlResponseRedirectViewFactory;

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

    private final String SCHEME_NAME = EidasScheme.stub_country.getEidasSchemeName();
    private final SessionId SESSION_ID = SessionId.createNewSessionId();
    private EidasSession session;

    @Mock
    private SessionRepository<EidasSession> sessionRepository;

    @Mock
    private EidasAuthnResponseService successAuthnResponseService;

    @Mock
    private SamlResponseRedirectViewFactory samlResponseRedirectViewFactory;

    @Mock
    private StubCountryRepository stubCountryRepository;

    @Mock
    private StubCountry stubCountry;

    @Before
    public void setUp(){
        resource = new EidasConsentResource(sessionRepository, successAuthnResponseService, successAuthnResponseService, samlResponseRedirectViewFactory, stubCountryRepository);

        EidasAuthnRequest eidasAuthnRequest = new EidasAuthnRequest("request-id", "issuer", "destination", "loa", Collections.emptyList());
        session = new EidasSession(SESSION_ID, eidasAuthnRequest, null, null, null, null, null);
        EidasUser user = new EidasUser("Jane", Optional.empty(), "Doe", Optional.empty(), "pid", new LocalDate(1990, 1, 2), null, null);
        session.setEidasUser(user);
        when(sessionRepository.get(SESSION_ID)).thenReturn(Optional.of(session));
        when(sessionRepository.deleteAndGet(SESSION_ID)).thenReturn(Optional.of(session));
    }

    @Test
    public void getShouldReturnASuccessfulResponseWhenSessionIsValid(){
        when(stubCountryRepository.getStubCountryWithFriendlyId(EidasScheme.fromString(SCHEME_NAME).get())).thenReturn(stubCountry);

        final Response response = resource.get(SCHEME_NAME, SESSION_ID);

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void postShouldReturnASuccessfulResponseWithRsaSha256SigningAlgorithmWhenSessionIsValid() {
        SamlResponseFromValue<org.opensaml.saml.saml2.core.Response> samlResponse = new SamlResponseFromValue<>(null, (r) -> null, null, null);
        when(successAuthnResponseService.getSuccessResponse(session, SCHEME_NAME)).thenReturn(samlResponse);
        when(samlResponseRedirectViewFactory.sendSamlMessage(samlResponse)).thenReturn(Response.ok().build());

        final Response response = resource.consent(SCHEME_NAME, "rsasha256","submit", SESSION_ID);

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void postShouldReturnASuccessfulResponseWithRsaSsaPsaSigningAlgorithmWhenSessionIsValid() {
        SamlResponseFromValue<org.opensaml.saml.saml2.core.Response> samlResponse = new SamlResponseFromValue<>(null, (r) -> null, null, null);
        when(successAuthnResponseService.getSuccessResponse(session, SCHEME_NAME)).thenReturn(samlResponse);
        when(samlResponseRedirectViewFactory.sendSamlMessage(samlResponse)).thenReturn(Response.ok().build());

        final Response response = resource.consent(SCHEME_NAME, "rsassa-pss","submit", SESSION_ID);

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test(expected = InvalidSigningAlgorithmException.class)
    public void postShouldThrowAnExceptionWhenAnInvalidSigningAlgorithmIsUsed() {
        SamlResponseFromValue<org.opensaml.saml.saml2.core.Response> samlResponse = new SamlResponseFromValue<>(null, (r) -> null, null, null);
        when(successAuthnResponseService.getSuccessResponse(session, SCHEME_NAME)).thenReturn(samlResponse);
        when(samlResponseRedirectViewFactory.sendSamlMessage(samlResponse)).thenReturn(Response.ok().build());

        resource.consent(SCHEME_NAME, "rsa-sha384","submit", SESSION_ID);
    }


    @Test(expected = GenericStubIdpException.class)
    public void shouldThrowAGenericStubIdpExceptionWhenSessionIsEmpty(){
        resource.get(SCHEME_NAME, null);
    }

}

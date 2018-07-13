package uk.gov.ida.stub.idp.resources;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.domain.EidasAuthnRequest;
import uk.gov.ida.stub.idp.domain.EidasScheme;
import uk.gov.ida.stub.idp.domain.SamlResponseFromValue;
import uk.gov.ida.stub.idp.exceptions.IncompleteRegistrationException;
import uk.gov.ida.stub.idp.exceptions.InvalidDateException;
import uk.gov.ida.stub.idp.exceptions.InvalidSessionIdException;
import uk.gov.ida.stub.idp.exceptions.InvalidUsernameOrPasswordException;
import uk.gov.ida.stub.idp.exceptions.UsernameAlreadyTakenException;
import uk.gov.ida.stub.idp.repositories.EidasSession;
import uk.gov.ida.stub.idp.repositories.EidasSessionRepository;
import uk.gov.ida.stub.idp.repositories.StubCountryRepository;
import uk.gov.ida.stub.idp.resources.eidas.EidasRegistrationPageResource;
import uk.gov.ida.stub.idp.services.NonSuccessAuthnResponseService;
import uk.gov.ida.stub.idp.services.StubCountryService;
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
public class EidasRegistrationPageResourceTest {

    private final String STUB_COUNTRY = "stub-country";
    private final SessionId SESSION_ID = SessionId.createNewSessionId();
    private final String RELAY_STATE = "relayState";
    private final String SAML_REQUEST_ID = "samlRequestId";
    private EidasSession eidasSession;

    @BeforeClass
    public static void doALittleHackToMakeGuicierHappyForSomeReason() {
        JerseyGuiceUtils.reset();
    }

    private EidasRegistrationPageResource resource;

    @Mock
    private StubCountryRepository stubCountryRepository;
    @Mock
    private StubCountryService stubCountryService;
    @Mock
    private NonSuccessAuthnResponseService nonSuccessAuthnResponseService;
    @Mock
    private EidasSessionRepository sessionRepository;
    @Mock
    private EidasAuthnRequest eidasAuthnRequest;

    @Before
    public void createResource() {
        resource = new EidasRegistrationPageResource(
                stubCountryRepository,
                stubCountryService,
                new SamlResponseRedirectViewFactory(),
                nonSuccessAuthnResponseService,
                sessionRepository
        );

        eidasSession = new EidasSession(SESSION_ID, eidasAuthnRequest, RELAY_STATE, null, null, null, null);
        when(sessionRepository.get(SESSION_ID)).thenReturn(Optional.ofNullable(eidasSession));
        when(sessionRepository.deleteAndGet(SESSION_ID)).thenReturn(Optional.ofNullable(new EidasSession(SESSION_ID, eidasAuthnRequest, RELAY_STATE, null, null, null, null)));
        when(eidasAuthnRequest.getRequestId()).thenReturn(SAML_REQUEST_ID);
    }

    @Test
    public void shouldHaveStatusAuthnCancelledResponseWhenUserCancels(){
        when(nonSuccessAuthnResponseService.generateAuthnCancel(anyString(), anyString(), eq(RELAY_STATE))).thenReturn(new SamlResponseFromValue<String>("saml", Function.identity(), RELAY_STATE, URI.create("uri")));

        resource.post(STUB_COUNTRY, null, null, null, null, null, null, null, null, Cancel, SESSION_ID);

        verify(nonSuccessAuthnResponseService).generateAuthnCancel(STUB_COUNTRY, SAML_REQUEST_ID, RELAY_STATE);
    }

    @Test
    public void shouldHaveStatusSuccessResponseWhenUserRegisters() throws InvalidSessionIdException, IncompleteRegistrationException, InvalidDateException, UsernameAlreadyTakenException, InvalidUsernameOrPasswordException {

        final Response response = resource.post(STUB_COUNTRY, "bob", "", "jones", "", "2000-01-01", "username", "password", LEVEL_2, Register, SESSION_ID);

        assertThat(response.getStatus()).isEqualTo(303);
        verify(stubCountryService).createAndAttachIdpUserToSession(eq(EidasScheme.fromString(STUB_COUNTRY).get()), anyString(), anyString(), eq(eidasSession), anyString(), anyString(), anyString(), anyString(), anyString(), eq(LEVEL_2));
    }


}

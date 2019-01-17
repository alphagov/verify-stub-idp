package uk.gov.ida.stub.idp.csrf;

import com.google.common.collect.ImmutableMap;
import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.cookies.HmacValidator;
import uk.gov.ida.stub.idp.csrf.exceptions.CSRFBodyNotFoundException;
import uk.gov.ida.stub.idp.csrf.exceptions.CSRFCouldNotValidateSessionException;
import uk.gov.ida.stub.idp.csrf.exceptions.CSRFNoTokenInSessionException;
import uk.gov.ida.stub.idp.csrf.exceptions.CSRFTokenNotFoundException;
import uk.gov.ida.stub.idp.csrf.exceptions.CSRFTokenWasInvalidException;
import uk.gov.ida.stub.idp.repositories.EidasSession;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.SessionRepository;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ida.stub.idp.cookies.CookieNames.SECURE_COOKIE_NAME;
import static uk.gov.ida.stub.idp.cookies.CookieNames.SESSION_COOKIE_NAME;
import static uk.gov.ida.stub.idp.csrf.CSRFCheckProtectionFilter.CSRF_PROTECT_FORM_KEY;


@RunWith(MockitoJUnitRunner.class)
public class CSRFCheckProtectionFilterTest {

    private boolean isSecureCookieEnabled = true;
    @Mock
    private HmacValidator hmacValidator;
    @Mock
    private SessionRepository<IdpSession> idpSessionRepository;
    @Mock
    private SessionRepository<EidasSession> eidasSessionRepository;
    @Mock
    private ContainerRequestContext containerRequestContext;
    @Mock
    private IdpSession session;

    private SessionId sessionId;

    @BeforeClass
    public static void doALittleHackToMakeGuicierHappyForSomeReason() {
        JerseyGuiceUtils.reset();
    }

    @Before
    public void setUp() {
        sessionId = SessionId.createNewSessionId();
        Map<String, Cookie> cookies = ImmutableMap.of(
                SESSION_COOKIE_NAME, new NewCookie(SESSION_COOKIE_NAME, sessionId.toString()),
                SECURE_COOKIE_NAME, new NewCookie(SECURE_COOKIE_NAME, "secure-cookie")
        );
        when(containerRequestContext.getCookies()).thenReturn(cookies);
        when(hmacValidator.validateHMACSHA256("secure-cookie", sessionId.getSessionId())).thenReturn(true);
        when(idpSessionRepository.containsSession(sessionId)).thenReturn(true);
        when(idpSessionRepository.get(sessionId)).thenReturn(Optional.ofNullable(session));
        when(containerRequestContext.hasEntity()).thenReturn(true);
    }

    @Test(expected = CSRFCouldNotValidateSessionException.class)
    public void shouldValidateSession() throws Exception {
        final String csrfToken = "foo";
        final String entity = "a=1&b=2&c=3&"+CSRF_PROTECT_FORM_KEY+"="+csrfToken;
        when(containerRequestContext.getEntityStream()).thenReturn(new ByteArrayInputStream(entity.getBytes()));
        when(session.getCsrfToken()).thenReturn(csrfToken);

        when(hmacValidator.validateHMACSHA256("secure-cookie", sessionId.getSessionId())).thenReturn(false);

        new CSRFCheckProtectionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, isSecureCookieEnabled).filter(containerRequestContext);
    }

    @Test(expected = CSRFBodyNotFoundException.class)
    public void shouldValidateEntityExists() throws Exception {
        final String csrfToken = "foo";
        final String entity = "a=1&b=2&c=3&"+CSRF_PROTECT_FORM_KEY+"="+csrfToken;
        when(containerRequestContext.getEntityStream()).thenReturn(new ByteArrayInputStream(entity.getBytes()));
        when(session.getCsrfToken()).thenReturn(csrfToken);

        when(containerRequestContext.hasEntity()).thenReturn(false);

        new CSRFCheckProtectionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, isSecureCookieEnabled).filter(containerRequestContext);
    }

    @Test(expected = CSRFNoTokenInSessionException.class)
    public void shouldCheckTokenExistsInTheSession() throws Exception {
        final String csrfToken = "foo";
        final String entity = "a=1&b=2&c=3&"+CSRF_PROTECT_FORM_KEY+"="+csrfToken;
        when(containerRequestContext.getEntityStream()).thenReturn(new ByteArrayInputStream(entity.getBytes()));
        when(session.getCsrfToken()).thenReturn(null);

        when(containerRequestContext.hasEntity()).thenReturn(true);

        new CSRFCheckProtectionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, isSecureCookieEnabled).filter(containerRequestContext);
    }

    @Test(expected = CSRFTokenWasInvalidException.class)
    public void shouldCheckTokenWithTheOneInTheSession() throws Exception {
        final String csrfToken = "foo";
        final String entity = "a=1&b=2&c=3&"+CSRF_PROTECT_FORM_KEY+"=not_this_token";
        when(containerRequestContext.getEntityStream()).thenReturn(new ByteArrayInputStream(entity.getBytes()));
        when(session.getCsrfToken()).thenReturn(csrfToken);

        new CSRFCheckProtectionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, isSecureCookieEnabled).filter(containerRequestContext);
    }

    @Test(expected = CSRFTokenNotFoundException.class)
    public void shouldErrorIfTokenNotFound() throws Exception {
        final String csrfToken = "foo";
        final String entity = "a=1&b=2&c=3&";
        when(containerRequestContext.getEntityStream()).thenReturn(new ByteArrayInputStream(entity.getBytes()));
        when(session.getCsrfToken()).thenReturn(csrfToken);

        new CSRFCheckProtectionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, isSecureCookieEnabled).filter(containerRequestContext);
    }

    @Test
    public void shouldAddCSRFProtectionToAllForms() throws Exception {
        final String csrfToken = "foo";
        final String entity = "a=1&b=2&c=3&"+CSRF_PROTECT_FORM_KEY+"="+csrfToken;
        when(containerRequestContext.getEntityStream()).thenReturn(new ByteArrayInputStream(entity.getBytes()));
        when(session.getCsrfToken()).thenReturn(csrfToken);

        new CSRFCheckProtectionFilter(idpSessionRepository, eidasSessionRepository, hmacValidator, isSecureCookieEnabled).filter(containerRequestContext);

        ArgumentCaptor<InputStream> argumentCaptor = ArgumentCaptor.forClass(InputStream.class);
        verify(containerRequestContext, times(1)).setEntityStream(argumentCaptor.capture());
        assertThat(IOUtils.toByteArray(argumentCaptor.getValue())).isEqualTo(entity.getBytes());
    }


}

package uk.gov.ida.stub.idp.csrf;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.logging.MDC;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.cookies.HmacValidator;
import uk.gov.ida.stub.idp.csrf.exceptions.CSRFBodyNotFoundException;
import uk.gov.ida.stub.idp.csrf.exceptions.CSRFCouldNotValidateSessionException;
import uk.gov.ida.stub.idp.csrf.exceptions.CSRFNoTokenInSessionException;
import uk.gov.ida.stub.idp.csrf.exceptions.CSRFTokenNotFoundException;
import uk.gov.ida.stub.idp.csrf.exceptions.CSRFTokenWasInvalidException;
import uk.gov.ida.stub.idp.exceptions.SessionIdCookieNotFoundException;
import uk.gov.ida.stub.idp.repositories.EidasSession;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.SessionRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static uk.gov.ida.stub.idp.cookies.CookieNames.SECURE_COOKIE_NAME;
import static uk.gov.ida.stub.idp.cookies.CookieNames.SESSION_COOKIE_NAME;

/**
 * Use the "Synchronizer Token Pattern" to implement CSRF
 * see https://en.wikipedia.org/wiki/Cross-site_request_forgery
 */
public class CSRFCheckProtectionFilter implements ContainerRequestFilter {

    private final SessionRepository<IdpSession> idpSessionRepository;
    private final SessionRepository<EidasSession> eidasSessionRepository;
    private final HmacValidator hmacValidator;
    private final boolean isSecureCookieEnabled;

    public static final String CSRF_PROTECT_FORM_KEY = "csrf_protect";

    private enum Status {VERIFIED, ID_NOT_PRESENT, HASH_NOT_PRESENT, DELETED_SESSION, INVALID_HASH, NOT_FOUND };
    private static final String NO_CURRENT_SESSION_COOKIE_VALUE = "no-current-session";

    @Inject
    public CSRFCheckProtectionFilter(SessionRepository<IdpSession> idpSessionRepository,
                                     SessionRepository<EidasSession> eidasSessionRepository,
                                     HmacValidator hmacValidator,
                                     @Named("isSecureCookieEnabled") Boolean isSecureCookieEnabled) {
        this.idpSessionRepository = idpSessionRepository;
        this.eidasSessionRepository = eidasSessionRepository;
        this.hmacValidator = hmacValidator;
        this.isSecureCookieEnabled = isSecureCookieEnabled;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        final SessionId sessionId = getValidSessionId(requestContext);

        if(!requestContext.hasEntity()) {
            throw new CSRFBodyNotFoundException();
        }

        // slurp the request entity - treat it as an opaque string
        final String requestBody = new String(IOUtils.toByteArray(requestContext.getEntityStream()));

        // validate the csrf token
        for(String keyValue: requestBody.split("&")) {
            final String[] split = keyValue.split("=");
            if(split[0].equals(CSRF_PROTECT_FORM_KEY)) {
                final Optional<String> tokenInSession = getTokenFromSession(sessionId);

                if(!tokenInSession.isPresent()) {
                    throw new CSRFNoTokenInSessionException();
                }

                if(!tokenInSession.get().equals(split[1].trim())) {
                    throw new CSRFTokenWasInvalidException();
                }

                requestContext.setEntityStream(new ByteArrayInputStream(requestBody.getBytes()));
                return;
            }
        }

        throw new CSRFTokenNotFoundException();
    }


    protected Optional<String> getTokenFromSession(SessionId sessionId) {
        final Optional<IdpSession> idpSession = idpSessionRepository.get(sessionId);
        final Optional<EidasSession> eidasSession = eidasSessionRepository.get(sessionId);
        if(idpSession.isPresent()) {
            return Optional.ofNullable(idpSession.get().getCsrfToken());
        }
        if(eidasSession.isPresent()) {
            return Optional.ofNullable(eidasSession.get().getCsrfToken());
        }
        return Optional.empty();
    }

    protected SessionId getValidSessionId(ContainerRequestContext requestContext) {

        // Get SessionId from cookie
        final Optional<String> sessionCookie = Optional.ofNullable(getValueOfPossiblyNullCookie(requestContext.getCookies(), SESSION_COOKIE_NAME));
        // Get SessionId HMAC from cookie
        final Optional<String> secureCookie;
        if (isSecureCookieEnabled) {
            secureCookie = Optional.ofNullable(getValueOfPossiblyNullCookie(requestContext.getCookies(), SECURE_COOKIE_NAME));
        } else {
            secureCookie = Optional.empty();
        }

        if (!sessionCookie.isPresent()) {
            throw new SessionIdCookieNotFoundException("Unable to locate session from session cookie");
        } else {
            MDC.remove("SessionId");
            MDC.put("SessionId", sessionCookie.get());
        }

        final Status status;

        if (StringUtils.isEmpty(sessionCookie.get())) {
            status = Status.ID_NOT_PRESENT;
        } else if (isSecureCookieEnabled && (!secureCookie.isPresent() || StringUtils.isEmpty(secureCookie.get()))) {
            status = Status.HASH_NOT_PRESENT;
        } else if (isSecureCookieEnabled && NO_CURRENT_SESSION_COOKIE_VALUE.equals(secureCookie.get())) {
            status = Status.DELETED_SESSION;
        } else if (isSecureCookieEnabled && !hmacValidator.validateHMACSHA256(secureCookie.get(), sessionCookie.get())) {
            status = Status.INVALID_HASH;
        } else if (!idpSessionRepository.containsSession(new SessionId(sessionCookie.get())) && !eidasSessionRepository.containsSession(new SessionId(sessionCookie.get()))) {
            status = Status.NOT_FOUND;
        } else {
            status = Status.VERIFIED;
        }

        if(status != Status.VERIFIED) {
            throw new CSRFCouldNotValidateSessionException();
        }

        return new SessionId(sessionCookie.get());
    }

    private String getValueOfPossiblyNullCookie(Map<String, Cookie> cookies, String cookieName) {
        return cookies.containsKey(cookieName) ? cookies.get(cookieName).getValue() : null;
    }

}

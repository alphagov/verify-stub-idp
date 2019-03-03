package uk.gov.ida.stub.idp.filters;

import org.apache.commons.lang.StringUtils;
import org.jboss.logging.MDC;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.cookies.HmacValidator;
import uk.gov.ida.stub.idp.exceptions.InvalidSecureCookieException;
import uk.gov.ida.stub.idp.exceptions.SecureCookieNotFoundException;
import uk.gov.ida.stub.idp.exceptions.SessionIdCookieNotFoundException;
import uk.gov.ida.stub.idp.exceptions.SessionNotFoundException;
import uk.gov.ida.stub.idp.repositories.EidasSession;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.SessionRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import java.util.Optional;

import static java.util.Optional.empty;
import static uk.gov.ida.stub.idp.cookies.CookieNames.SECURE_COOKIE_NAME;
import static uk.gov.ida.stub.idp.cookies.CookieNames.SESSION_COOKIE_NAME;

public class SessionCookieValueMustExistAsASessionFilter implements ContainerRequestFilter {

    private final SessionRepository<IdpSession> idpSessionRepository;
    private final SessionRepository<EidasSession> eidasSessionRepository;
    private final HmacValidator hmacValidator;
    private final boolean isSecureCookieEnabled;

    public enum Status {VERIFIED, ID_NOT_PRESENT, HASH_NOT_PRESENT, DELETED_SESSION, INVALID_HASH, NOT_FOUND }

    public static final String NO_CURRENT_SESSION_COOKIE_VALUE = "no-current-session";

    @Inject
    public SessionCookieValueMustExistAsASessionFilter(SessionRepository<IdpSession> idpSessionRepository,
                                                       SessionRepository<EidasSession> eidasSessionRepository,
                                                       HmacValidator hmacValidator,
                                                       @Named("isSecureCookieEnabled") Boolean isSecureCookieEnabled) {
        this.idpSessionRepository = idpSessionRepository;
        this.eidasSessionRepository = eidasSessionRepository;
        this.hmacValidator = hmacValidator;
        this.isSecureCookieEnabled = isSecureCookieEnabled;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {

        // Get SessionId from cookie
        final Optional<String> sessionCookie = getValueOfPossiblyNullCookie(requestContext.getCookies().getOrDefault(SESSION_COOKIE_NAME, null));
        // Get SessionId HMAC from cookie
        final Optional<String> secureCookie;
        if (isSecureCookieEnabled) {
            secureCookie = getValueOfPossiblyNullCookie(requestContext.getCookies().getOrDefault(SECURE_COOKIE_NAME, null));
        } else {
            secureCookie = empty();
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

        switch (status) {
            case VERIFIED:
                break;
            case ID_NOT_PRESENT:
                throw new SessionIdCookieNotFoundException("Session id was Null or Empty, cannot match to the secure cookie.");
            case HASH_NOT_PRESENT:
                throw new SecureCookieNotFoundException("Secure cookie not found.", new SessionId(sessionCookie.get()));
            case DELETED_SESSION:
                throw new InvalidSecureCookieException("Secure cookie was set to deleted session value, indicating a previously completed session.", new SessionId(sessionCookie.get()));
            case INVALID_HASH:
                throw new InvalidSecureCookieException("Secure cookie value not valid.", new SessionId(sessionCookie.get()));
            case NOT_FOUND:
                throw new SessionNotFoundException("Session not found - restart journey.", new SessionId(sessionCookie.get()));
            default:
                throw new IllegalStateException("Impossible to get here");
        }
    }

    private Optional<String> getValueOfPossiblyNullCookie(Cookie cookie) {
        return Optional.ofNullable(cookie).map(Cookie::getValue);
    }
}

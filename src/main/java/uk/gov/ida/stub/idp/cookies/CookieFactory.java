package uk.gov.ida.stub.idp.cookies;

import com.google.inject.Inject;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.common.shared.configuration.SecureCookieConfiguration;
import uk.gov.ida.common.shared.security.HmacDigest;

import javax.inject.Singleton;
import javax.ws.rs.core.NewCookie;

@Singleton
public class CookieFactory {
    private final HmacDigest hmacDigest;
    private final SecureCookieConfiguration secureCookieConfiguration;

    @Inject
    public CookieFactory(HmacDigest hmacDigest, SecureCookieConfiguration secureCookieConfiguration) {
        this.hmacDigest = hmacDigest;
        this.secureCookieConfiguration = secureCookieConfiguration;
    }

    public NewCookie createSecureCookieWithSecurelyHashedValue(SessionId sessionId) {
        String hashedSessionId = hmacDigest.digest(sessionId.toString());
        return createSecureCookieWithValue(hashedSessionId);
    }

    public NewCookie createSessionIdCookie(SessionId sessionId){
        return new HttpOnlyNewCookie(
                CookieNames.SESSION_COOKIE_NAME,
                sessionId.toString(),
                "/",
                "",
                NewCookie.DEFAULT_MAX_AGE,
                secureCookieConfiguration.isSecure());
    }

    private NewCookie createSecureCookieWithValue(String value) {
        return new HttpOnlyNewCookie(
                CookieNames.SECURE_COOKIE_NAME,
                value,
                "/",
                "",
                NewCookie.DEFAULT_MAX_AGE,
                secureCookieConfiguration.isSecure());
    }
}

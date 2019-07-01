package uk.gov.ida.stub.idp.cookies;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.common.shared.configuration.SecureCookieConfiguration;
import uk.gov.ida.common.shared.security.HmacDigest;

import javax.ws.rs.core.NewCookie;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CookieFactoryTest {

    private final int SESSION_COOKIE_MAX_AGE = -1;
    private final boolean IS_SECURE = true;

    @Mock
    HmacDigest hmacDigest;

    @Mock
    SecureCookieConfiguration secureCookieConfiguration;

    CookieFactory cookieFactory;

    @BeforeClass
    public static void doALittleHackToMakeGuicierHappyForSomeReason() {
        JerseyGuiceUtils.reset();
    }

    @Before
    public void setup() {
        cookieFactory = new CookieFactory(hmacDigest, secureCookieConfiguration);
        when(secureCookieConfiguration.isSecure()).thenReturn(IS_SECURE);
    }

    @Test
    public void createSecureCookieWithSecurelyHashedValue_shouldCreateACookie() {
        SessionId sessionId = new SessionId(UUID.randomUUID().toString());

        String digestedValue = "ummm cookie";
        when(hmacDigest.digest(sessionId.toString())).thenReturn(digestedValue);


        NewCookie cookie = cookieFactory.createSecureCookieWithSecurelyHashedValue(sessionId);

        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEqualTo(digestedValue);
        assertThat(cookie.getMaxAge()).isEqualTo(SESSION_COOKIE_MAX_AGE);
        assertThat(cookie.isSecure()).isEqualTo(IS_SECURE);
        assertIsHttpOnly(cookie);
    }

    @Test
    public void createSessionIdCookie_shouldCreateACookie() {

        when(secureCookieConfiguration.isSecure()).thenReturn(IS_SECURE);
        SessionId expectedValue = SessionId.createNewSessionId();

        NewCookie cookie = cookieFactory.createSessionIdCookie(expectedValue);

        assertThat(cookie.getName()).isEqualTo(CookieNames.SESSION_COOKIE_NAME);
        assertThat(cookie.getValue()).isEqualTo(expectedValue.toString());
        assertThat(cookie.getMaxAge()).isEqualTo(SESSION_COOKIE_MAX_AGE);
        assertThat(cookie.isSecure()).isEqualTo(IS_SECURE);
        assertIsHttpOnly(cookie);
    }

    private void assertIsHttpOnly(NewCookie deletedSecureCookie) {
        assertThat(deletedSecureCookie.toString()).contains(" HttpOnly");
    }
}

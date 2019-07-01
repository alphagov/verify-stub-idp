package uk.gov.ida.stub.idp.cookies;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.common.shared.security.HmacDigest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HmacValidatorTest {

    @Mock
    private HmacDigest hmacDigest;

    @Test
    public void shouldReturnTrueWhenSessionCookieAndSecureCookieMatchUp() {
        SessionId sessionId = SessionId.createNewSessionId();
        String secureCookie = "secure-cookie";
        String hmacDouble = "hmac-double";
        when(hmacDigest.digest(sessionId.getSessionId())).thenReturn("session-id-hmac");
        when(hmacDigest.digest("session-id-hmac")).thenReturn(hmacDouble);
        when(hmacDigest.digest(secureCookie)).thenReturn(hmacDouble);
        HmacValidator hmacValidator = new HmacValidator(hmacDigest);
        assertThat(hmacValidator.validateHMACSHA256(secureCookie, sessionId.getSessionId())).isEqualTo(true);
    }

    @Test
    public void shouldReturnFalseWhenSessionCookieAndSecureCookieDontMatchUp() {
        SessionId sessionId = SessionId.createNewSessionId();
        String secureCookie = "secure-cookie";
        when(hmacDigest.digest(sessionId.getSessionId())).thenReturn("session-id-hmac");
        when(hmacDigest.digest("session-id-hmac")).thenReturn("session-id-hmac-double");
        when(hmacDigest.digest(secureCookie)).thenReturn("secure-cookie-hmac");
        HmacValidator hmacValidator = new HmacValidator(hmacDigest);
        assertThat(hmacValidator.validateHMACSHA256(secureCookie, sessionId.getSessionId())).isEqualTo(false);
    }
}

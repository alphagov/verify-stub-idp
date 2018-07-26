package uk.gov.ida.stub.idp.cookies;

import com.google.inject.Inject;
import uk.gov.ida.common.shared.security.HmacDigest;

public class HmacValidator {
    private HmacDigest hmacDigest;

    @Inject
    public HmacValidator(HmacDigest hmacDigest) {
        this.hmacDigest = hmacDigest;
    }

    // Do a double HMAC verification to avoid a potential timing related side-channel
    // See https://www.isecpartners.com/blog/2011/february/double-hmac-verification.aspx
    public boolean validateHMACSHA256(final String cookieValue, final String sessionId) {
        final String sessionIdHmac = hmacDigest.digest(sessionId);
        final String sessionIdHmacDouble = hmacDigest.digest(sessionIdHmac);

        final String cookieValueHmacDouble = hmacDigest.digest(cookieValue);

        return cookieValueHmacDouble.equals(sessionIdHmacDouble);
    }
}

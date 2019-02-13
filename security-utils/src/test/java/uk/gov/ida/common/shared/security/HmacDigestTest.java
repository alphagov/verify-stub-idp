package uk.gov.ida.common.shared.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.shared.configuration.SecureCookieKeyStore;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HmacDigestTest {

    @Mock
    private HmacDigest.HmacSha256MacFactory macFactory;

    @Mock
    private SecureCookieKeyStore secureCookieKeyStore;

    private final String key = "this-is-my-secret-key";
    private final Key secretKey = new SecretKeySpec("this-is-my-secret-key".getBytes(), "HmacSHA256");

    @Test
    public void digest_shouldDDigestAValueUsingHmac() throws Exception {
        //expected hmac generated on the shell using:
        //echo -n 'string to be encoded' | openssl dgst -sha256 -hmac 'this-is-my-secret-key' | xxd -r -p | base64
        final String expectedHMAC = "Twk5x/dX6Dh/2m1S6PC6uS4V//JTeU56oW2sm1CSZ8g=";

        when(secureCookieKeyStore.getKey()).thenReturn(secretKey);
        HmacDigest digest = new HmacDigest(new HmacDigest.HmacSha256MacFactory(), secureCookieKeyStore);
        String result = digest.digest("string to be encoded");
        assertThat(result).isEqualTo(expectedHMAC);
    }
}

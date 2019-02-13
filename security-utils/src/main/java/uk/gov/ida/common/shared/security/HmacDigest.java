package uk.gov.ida.common.shared.security;

import uk.gov.ida.common.shared.configuration.SecureCookieKeyStore;

import javax.crypto.Mac;
import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.google.common.base.Throwables.propagate;

public class HmacDigest {

    private final HmacSha256MacFactory hmacSha256MacFactory;
    private final SecureCookieKeyStore secureCookieKeyStore;

    @Inject
    public HmacDigest(HmacSha256MacFactory hmacSha256MacFactory, SecureCookieKeyStore secureCookieKeyStore) {
        this.hmacSha256MacFactory = hmacSha256MacFactory;
        this.secureCookieKeyStore = secureCookieKeyStore;
    }

    public String digest(String toEncode) {
        Mac mac;
        try {
            mac = hmacSha256MacFactory.getInstance();
        } catch (NoSuchAlgorithmException e) {
            throw propagate(e);
        }

        try {
            mac.init(secureCookieKeyStore.getKey());
        } catch (InvalidKeyException e) {
            throw propagate(e);
        }

        byte[] bytes;
        try {
            bytes = mac.doFinal(toEncode.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw propagate(e);
        }

        return StringEncoding.toBase64Encoded(bytes);
    }

    public static class HmacSha256MacFactory {
        public Mac getInstance() throws NoSuchAlgorithmException {
            return Mac.getInstance("HmacSHA256");
        }
    }
}

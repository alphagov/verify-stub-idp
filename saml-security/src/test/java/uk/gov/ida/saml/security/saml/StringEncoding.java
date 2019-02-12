package uk.gov.ida.saml.security.saml;

import org.apache.commons.codec.binary.Base64;

import static org.apache.commons.codec.binary.StringUtils.getBytesUtf8;
import static org.apache.commons.codec.binary.StringUtils.newStringUtf8;

public abstract class StringEncoding {

    public static String toBase64Encoded(String unencodedString) {
        return newStringUtf8(Base64.encodeBase64(getBytesUtf8(unencodedString)));
    }

    public static String fromBase64Encoded(String encodedString) {
        return newStringUtf8(Base64.decodeBase64(encodedString));
    }
}

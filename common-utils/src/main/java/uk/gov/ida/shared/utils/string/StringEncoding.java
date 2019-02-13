package uk.gov.ida.shared.utils.string;

import com.google.common.base.Throwables;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static org.apache.commons.codec.binary.StringUtils.getBytesUtf8;
import static org.apache.commons.codec.binary.StringUtils.newStringUtf8;

public abstract class StringEncoding {

    public static final String ENCODING = "UTF-8";

    public static String toBase64Encoded(String unencodedString) {
        return newStringUtf8(Base64.encodeBase64(getBytesUtf8(unencodedString)));
    }

    public static String toBase64Encoded(byte[] bytes) {
        return newStringUtf8(Base64.encodeBase64(bytes));
    }

    public static String fromBase64Encoded(String encodedString) {
        return newStringUtf8(Base64.decodeBase64(encodedString));
    }

    public static byte[] fromBase64ToByteArrayEncoded(String encodedString) {
        return Base64.decodeBase64(encodedString);
    }

    public static String urlEncode(String input) {
        String encodedValue;
        try {
            encodedValue = URLEncoder.encode(input, ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw Throwables.propagate(e);
        }
        return encodedValue;
    }

    public static String urlDecode(String input) {
        String decodedValue;
        try {
            decodedValue = URLDecoder.decode(input, ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw Throwables.propagate(e);
        }
        return decodedValue;
    }

    public static String nullSafeUrlDecode(String urlEncodedValue) {
        String urlDecodedValue = null;
        if (urlEncodedValue != null) {
            urlDecodedValue = urlDecode(urlEncodedValue);
        }
        return urlDecodedValue;
    }
}

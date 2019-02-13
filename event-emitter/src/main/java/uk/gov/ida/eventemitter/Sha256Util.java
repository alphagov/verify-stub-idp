package uk.gov.ida.eventemitter;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256Util {

    private static final int HASH_SIZE = 64;

    public String hash(String... values) {

        if (values == null || values.length == 0) {
            throw new IllegalArgumentException();
        }

        MessageDigest messageDigest;

        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] hash;

        try {
            for (String value : values) {
                messageDigest.update(value.getBytes("UTF-8"));
            }
            hash = messageDigest.digest();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        StringBuilder hexString = new StringBuilder(HASH_SIZE);

        for (byte byteValue : hash) {
            String hex = Integer.toHexString(0xff & byteValue);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
}

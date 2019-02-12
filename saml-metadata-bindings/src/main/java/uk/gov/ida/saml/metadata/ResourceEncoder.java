package uk.gov.ida.saml.metadata;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;

public class ResourceEncoder {
    public static String entityIdAsResource(String entityId) {
        return Hex.encodeHexString(entityId.getBytes(StandardCharsets.UTF_8));
    }
}

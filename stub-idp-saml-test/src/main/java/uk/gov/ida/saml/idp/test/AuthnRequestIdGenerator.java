package uk.gov.ida.saml.idp.test;

import java.util.UUID;

public final class AuthnRequestIdGenerator {
    private AuthnRequestIdGenerator() {}

    public static String generateRequestId() {
        return "_" + UUID.randomUUID().toString();
    }
}

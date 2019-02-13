package uk.gov.ida.common.shared.configuration;

import java.security.Key;

public interface SecureCookieKeyStore {
    Key getKey();
}

package uk.gov.ida.common.shared.security;

import java.security.PublicKey;
import java.util.List;

public interface InternalPublicKeyStore {
    List<PublicKey> getVerifyingKeysForEntity();
}

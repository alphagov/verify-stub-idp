package uk.gov.ida.common.shared.security;

import java.io.InputStream;

public interface PublicKeyInputStreamFactory {
    InputStream createInputStream(String publicKeyUri);
}

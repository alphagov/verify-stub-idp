package uk.gov.ida.saml.security;

import org.opensaml.security.credential.Credential;

public interface EncryptionCredentialResolver {
    Credential getEncryptingCredential(String receiverId);
}

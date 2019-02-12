package uk.gov.ida.saml.security.validators;

import org.opensaml.saml.saml2.core.EncryptedAssertion;

import java.util.List;

public interface ValidatedEncryptedAssertionContainer {
    List<EncryptedAssertion> getEncryptedAssertions();
}

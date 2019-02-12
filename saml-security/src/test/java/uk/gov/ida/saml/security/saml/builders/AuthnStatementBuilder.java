package uk.gov.ida.saml.security.saml.builders;

import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnStatement;
import uk.gov.ida.saml.core.extensions.IdaAuthnContext;
import uk.gov.ida.saml.security.saml.TestSamlObjectFactory;

import java.util.Optional;

public class AuthnStatementBuilder {

    private static TestSamlObjectFactory testSamlObjectFactory = new TestSamlObjectFactory();

    private Optional<AuthnContext> authnContext = Optional.ofNullable(getAuthnContext());

    public static AuthnStatementBuilder anAuthnStatement() {
        return new AuthnStatementBuilder();
    }

    public AuthnStatement build() {
        AuthnStatement authnStatement = testSamlObjectFactory.createAuthnStatement();
        authnContext.ifPresent(authnStatement::setAuthnContext);

        return authnStatement;
    }

    private AuthnContext getAuthnContext() {
        AuthnContext authnContext = new TestSamlObjectFactory().createAuthnContext();
        authnContext.setAuthnContextClassRef(testSamlObjectFactory.createAuthnContextClassReference(IdaAuthnContext.LEVEL_2_AUTHN_CTX));
        return authnContext;
    }
}

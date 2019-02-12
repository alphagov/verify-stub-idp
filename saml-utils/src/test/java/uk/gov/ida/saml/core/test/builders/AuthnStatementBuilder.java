package uk.gov.ida.saml.core.test.builders;

import org.joda.time.DateTime;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnStatement;
import uk.gov.ida.saml.core.extensions.EidasAuthnContext;
import uk.gov.ida.saml.core.test.OpenSamlXmlObjectFactory;

import java.util.Optional;

import static uk.gov.ida.saml.core.test.builders.AuthnContextBuilder.anAuthnContext;
import static uk.gov.ida.saml.core.test.builders.AuthnContextClassRefBuilder.anAuthnContextClassRef;

public class AuthnStatementBuilder {

    private static OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();

    private Optional<AuthnContext> authnContext = Optional.ofNullable(anAuthnContext().build());
    private Optional<DateTime> authnInstant = Optional.of(DateTime.now());

    public static AuthnStatementBuilder anAuthnStatement() {
        return new AuthnStatementBuilder();
    }

    public static AuthnStatementBuilder anEidasAuthnStatement() {
        return anEidasAuthnStatement(EidasAuthnContext.EIDAS_LOA_SUBSTANTIAL);
    }

    public static AuthnStatementBuilder anEidasAuthnStatement(String level) {
        return anAuthnStatement()
            .withAuthnContext(
                anAuthnContext()
                    .withAuthnContextClassRef(
                        anAuthnContextClassRef()
                            .withAuthnContextClasRefValue(level).build())
                    .build()
            );
    }

    public AuthnStatement build() {
        AuthnStatement authnStatement = openSamlXmlObjectFactory.createAuthnStatement();

        if (authnContext.isPresent()) {
            authnStatement.setAuthnContext(authnContext.get());
        }

        if (authnInstant.isPresent()) {
            authnStatement.setAuthnInstant(authnInstant.get());
        }

        return authnStatement;
    }

    public AuthnStatementBuilder withAuthnContext(AuthnContext authnContext) {
        this.authnContext = Optional.ofNullable(authnContext);
        return this;
    }

    public AuthnStatementBuilder withAuthnInstant(DateTime authnInstant) {
        this.authnInstant = Optional.ofNullable(authnInstant);
        return this;
    }

    public AuthnStatementBuilder withId(String s) {
        throw new UnsupportedOperationException("Implement Me!");
    }
}

package uk.gov.ida.saml.core.validation.errors;

import uk.gov.ida.saml.core.validation.SamlDocumentReference;

import static java.text.MessageFormat.format;

public class AuthnContextMissingError extends SamlValidationSpecification {

    public static final String MISSING_AUTHN_CONTEXT = "Assertion is missing 'AuthnContext' element.";

    public AuthnContextMissingError(String message, boolean contextExpected, Object... params) {
        super(format(message, params), contextExpected);
    }

    @Override
    public SamlDocumentReference documentReference() {
        return SamlDocumentReference.hubProfile11a("2.1.3.25");
    }
}

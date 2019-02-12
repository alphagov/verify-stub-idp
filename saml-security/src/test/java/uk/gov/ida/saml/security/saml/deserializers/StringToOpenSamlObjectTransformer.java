package uk.gov.ida.saml.security.saml.deserializers;

import org.opensaml.saml.saml2.core.AuthnRequest;
import uk.gov.ida.saml.security.saml.StringEncoding;

import java.util.function.Function;

import static java.util.regex.Pattern.matches;

public class StringToOpenSamlObjectTransformer implements Function<String, AuthnRequest> {

    private final AuthnRequestUnmarshaller authnRequestUnmarshaller;

    public StringToOpenSamlObjectTransformer(
            final AuthnRequestUnmarshaller authnRequestUnmarshaller) {
        this.authnRequestUnmarshaller = authnRequestUnmarshaller;
    }

    @Override
    public AuthnRequest apply(final String input) {
        if (input == null) {
            throw new RuntimeException("SAML was null");
        }
        final String decodedInput = decode(input);
        return authnRequestUnmarshaller.fromString(decodedInput);
    }

    private String decode(String input) {
        String withoutWhitespace = input.replaceAll("\\s", "");
        if (!matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$", withoutWhitespace)) {
            throw new RuntimeException("Invalid Base64 string");
        }
        return StringEncoding.fromBase64Encoded(withoutWhitespace);
    }

}

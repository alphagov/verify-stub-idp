package uk.gov.ida.saml.core.validation.errors;

import uk.gov.ida.saml.core.validation.SamlDocumentReference;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;

import java.text.MessageFormat;

public class SamlValidationSpecification extends SamlValidationSpecificationFailure {

    public static final String INVALID_BASE64_ENCODING = "SAML is not base64 encoded in message body. start> {0} <end";
    public static final String MISSING_SAML = "Missing SAML message.";
    public static final String DESERIALIZATION_ERROR = "Unable to deserialize string into OpenSaml object: {0}";
    public static final String UNMARSHALLING_ERROR = "Unable to unmarshall element ''{0}'' into OpenSaml object.";

    public SamlValidationSpecification(String errorFormat, Object... params) {
        super(MessageFormat.format(errorFormat, params), false);
    }

    @Override
    public SamlDocumentReference documentReference() {
        return SamlDocumentReference.samlCore20("");
    }
}

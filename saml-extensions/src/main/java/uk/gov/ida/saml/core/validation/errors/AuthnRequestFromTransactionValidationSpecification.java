package uk.gov.ida.saml.core.validation.errors;

import uk.gov.ida.saml.core.validation.SamlDocumentReference;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;

import static java.text.MessageFormat.format;

public class AuthnRequestFromTransactionValidationSpecification extends SamlValidationSpecificationFailure {
    public static final String ASSERTION_CONSUMING_SERVICE_URL_EXISTS = "''AssertionConsumerServiceURL'' must not be present.";
    public static final String SCOPE_NOT_ALLOWED = "''Scoping'' element is not allowed.";
    public static final String PROTOCOL_BINDING = "''ProtocolBinding'' has illegal value: {0}. Value must be set to {1}.";
    public static final String UNRECOGNISED_BINDING = "{0} is not a recognised binding.";
    public static final String PASSIVE_NOT_ALLOWED = "''IsPassive'' attribute is not allowed.";
    public static final String ASSERTION_CONSUMING_SERVICE_INDEX_INVALID = "Invalid assertion consumer index {0}";

    public static final String MISSING_ID = "SAML is missing ''ID'' attribute.";
    public static final String INCORRECT_VERSION = "SAML 'Version Number' has incorrect value. Should be 2.0.";
    public static final String EMPTY_ISSUER = "SAML ''Issuer'' element has no value.";
    public static final String MISSING_ISSUER = "SAML is missing 'Issuer' element.";
    public static final String MISSING_ISSUE_INSTANT = "Assertion with id {0} has no ''IssueInstant'' attribute";
    public static final String MISSING_VERSION = "Assertion with id {0} has no ''Version'' attribute";

    public static final String MISSING_NAME_ID_POLICY = "Name ID policy format is missing.";
    public static final String ILLEGAL_NAME_ID_POLICY = "Name ID policy format has illegal value: {0}.";

    public AuthnRequestFromTransactionValidationSpecification(String message, Object... params) {
        super(format(message, params), true);
    }

    @Override
    public SamlDocumentReference documentReference() {
        return SamlDocumentReference.hubProfile11a("2.1.4.1");
    }
}

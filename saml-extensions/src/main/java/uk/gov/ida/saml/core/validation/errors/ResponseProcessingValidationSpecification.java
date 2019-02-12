package uk.gov.ida.saml.core.validation.errors;

import uk.gov.ida.saml.core.validation.SamlDocumentReference;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;

import static java.text.MessageFormat.format;

public class ResponseProcessingValidationSpecification extends SamlValidationSpecificationFailure {

    public static final String MISSING_ATTRIBUTE_STATEMENT_IN_ASSERTION = "All <Assertions> must contain an <AttributeStatement>. Assertion Id: {0} doesn''t.";
    public static final String MISSING_AUTHN_STATEMENT = "Assertion is missing 'AuthnStatement' element.";
    public static final String MULTIPLE_AUTHN_STATEMENTS = "Response contains multiple authn statements.";
    public static final String ATTRIBUTE_STATEMENT_EMPTY = "The assertion MUST contain an <AttributeStatement> with at least one <Attribute> element. Assertion Id: {0} doesn''t.";
    public static final String MISSING_AUTHN_CONTEXT_CLASS_REF = "Assertion is missing 'AuthnContextClassRef' element.";
    public static final String MISSING_AUTHN_CONTEXT_CLASS_REF_VALUE = "Assertion is missing 'AuthnContextClassRef' value.";
    public static final String AUTHN_STATEMENT_ALREADY_RECEIVED = "An authn statement assertion with id {0} has already been received.";

    public static final String MISSING_SUBJECT = "Assertion with id {0} has no subject.";
    public static final String SUBJECT_HAS_NO_NAME_ID = "Subject for assertion with id {0} has no name id.";
    public static final String MISSING_SUBJECT_NAME_ID_FORMAT = "Subject''s name id format for assertion with id {0} is missing.";
    public static final String ILLEGAL_SUBJECT_NAME_ID_FORMAT = "Subject''s name id format for assertion with id {0} has illegal value {1}.";

    public static final String UNENCRYPTED_ASSERTIONS = "Response has unencrypted assertion.";
    public static final String UNREQUIRED_ENCRYPTION = "Response contained an encrypted assertion. Based on configuration, encryption is not required for this environment.";
    public static final String UNEXPECTED_ENCRYPTED_ASSERTIONS = "Response contains encrypted assertions. There should be no encrypted assertions remaining post decryption.";
    public static final String MISSING_SUCCESS_UNENCRYPTED = "Success response has no unencrypted assertions.";
    public static final String NON_SUCCESS_HAS_UNENCRYPTED = "Non-success response has unencrypted assertions. Should contain no assertions.";
    public static final String MISSING_INRESPONSE_TO = "Response has no 'InResponseTo' attribute.";
    public static final String EMPTY_INRESPONSE_TO = "Response 'InResponseTo' attribute is empty.";

    public static final String STATUS_CODE_MUST_BE_EITHER = "''{0}'' response sub status must be either ''{1}'' or ''{2}''.";
    public static final String STATUS_CODE_MUST_BE_ONE_OF = "''{0}'' response sub status must be ''{1}'', ''{2}'', or ''{3}''.";
    public static final String MISSING_SUB_STATUS = "Response requires a sub status.";
    public static final String UNEXPECTED_NUMBER_OF_ASSERTIONS = "Response expected to contain {0} assertions. {1} assertion(s) found.";

    public ResponseProcessingValidationSpecification(String message, Object... parameters) {
        super(format(message, parameters), true);
    }

    @Override
    public SamlDocumentReference documentReference() {
        return SamlDocumentReference.hubProfile11a("2.1.4.2");
    }
}

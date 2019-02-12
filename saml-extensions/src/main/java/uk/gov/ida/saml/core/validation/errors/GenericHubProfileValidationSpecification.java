package uk.gov.ida.saml.core.validation.errors;

import uk.gov.ida.saml.core.validation.SamlDocumentReference;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;

import java.text.MessageFormat;

public class GenericHubProfileValidationSpecification extends SamlValidationSpecificationFailure {

    public static final String INVALID_MESSAGE_SIGNATURE = "Signature was not valid.";
    public static final String INVALID_ASSERTION_SIGNATURE = "Signature for assertion {0} was not valid.";
    public static final String UNABLE_TO_VALIDATE_MESSAGE_SIGNATURE = "Unknown problem when trying to validate signature.";
    public static final String UNABLE_TO_VALIDATE_ASSERTION_SIGNATURE = "Unknown problem when trying to validate signature for assertion {0}.";
    public static final String MISSING_SIGNATURE = "Message has no signature.";
    public static final String MISSING_ASSERTION_SIGNATURE = "Assertion with id {0} has no signature.";
    public static final String SIGNATURE_NOT_SIGNED = "Message signature is not signed";
    public static final String ASSERTION_SIGNATURE_NOT_SIGNED = "Assertion with id {0} is not signed";

    public static final String UNABLE_TO_DECRYPT = "Problem decrypting assertion {0}.";
    public static final String UNSUPPORTED_SIGNATURE_ENCRYPTION_ALGORITHM = "Signature algorithm {0} is not supported.";
    public static final String ENCRYPTION_ALGORITHM_SHOULD_BE_AES128 = "Assertion encrypted with unsupported encryption algorithm {0}, should be AES128.";
    public static final String UNABLE_TO_LOCATE_ENCRYPTED_KEY = "Unable to located encrypted key within assertion.";
    public static final String KEY_ENCRYPTION_ALGORITHM_SHOULD_BE_RSAOAEP = "Key encrypted with unsupported encryption algorithm {0}, should be RSAOAEP.";


    public static final String MISSING_KEY = "Could not find expected key for {0} for entity {1}.";
    public static final String UNSUPPORTED_KEY = "Unspecified key descriptor usage type: {0} is not supported.";
    public static final String MISSING_ROLE_DESCRIPTOR = "EntityDescriptor is missing a 'RoleDescriptor' element.";
    public static final String MISSING_KEY_DESCRIPTOR = "RoleDescriptor is missing a ''KeyDescriptor'' element.";
    public static final String MISSING_KEY_INFO = "KeyDescriptor is missing a ''KeyInfo'' element.";
    public static final String MISSING_X509DATA = "KeyInfo is missing a ''X509Data'' element.";
    public static final String MISSING_X509CERT = "X509Data is missing a ''X509Certificate'' element.";
    public static final String EMPTY_X509CERT = "X509Certificate element is empty.";

    public static final String MISSING_ID = "SAML is missing ''ID'' attribute.";

    public static final String MISSING_ISSUER = "SAML is missing ''Issuer'' element.";
    public static final String ILLEGAL_ISSUER_FORMAT = "SAML ''Issuer'' has wrong format: {0}, expected {1}.";
    public static final String EMPTY_ISSUER = "SAML ''Issuer'' element has no value.";

    public static final String MISSING_OR_EMPTY_ENTITY_ID = "SAML ''EntityID'' attribute is missing or has no value.";

    public static final String MISSING_DESTINATION = "Destination should not be absent. Expected: {0}.";
    public static final String EMPTY_DESTINATION = "Destination is incorrect. Expected: {0} Received: {1}.";


    public static final String MISSING_ISSUE_INSTANT = "Assertion with id {0} has no ''IssueInstant'' attribute";
    public static final String MISSING_VERSION = "Assertion with id {0} has no ''Version'' attribute";
    public static final String ILLEGAL_VERSION = "Assertion with id {0} declared an illegal ''Version'' attribute value";

    public static final String NO_SUBJECT_CONF_WITH_BEARER_METHOD = "Assertion with id {0} has no subject confirmation with method set to bearer.";
    public static final String EMPTY_IP_ADDRESS = "Assertion with id {0} IP address attribute contains no value.";
    public static final String MISSING_IP_ADDRESS = "Assertion with id {0} is missing an IP address attribute.";
    public static final String DUPLICATE_MATCHING_DATASET = "A matching dataset assertion with id {0} has already been received for idp {1}";
    public static final String MDS_STATEMENT_MISSING = "Matching dataset contains no attribute statements.";
    public static final String MDS_MULTIPLE_STATEMENTS = "Matching dataset contains multiple attribute statements.";
    public static final String MISSING_MDS = "Missing a matching dataset assertion.";

    public static final String MISMATCHED_PIDS = "IDP matching dataset and authn statement assertions do not contain matching persistent identifiers";
    public static final String MISMATCHED_ISSUERS = "IDP matching dataset and authn statement assertions do not contain matching issuers";

    public static final String MDS_ATTRIBUTE_NOT_RECOGNISED = "''{0}'' is not a recognised matching dataset attribute.";
    public static final String EMPTY_ATTRIBUTE = "Attribute ''{0}'' does not have a value.";
    public static final String ATTRIBUTE_HAS_INCORRECT_TYPE = "Attribute ''{0}'' has incorrect type. Should be ''{1}'' but got ''{2}''.";

    public static final String SUB_STATUS_CODE_LIMIT_EXCEEDED = "More than {0} nested sub-status codes are not permitted.";
    public static final String INVALID_STATUS_CODE = "Status code ''{0}'' is not a valid status code.";
    public static final String INVALID_SUB_STATUS_CODE = "Sub-status ''{0}'' is not a valid sub-status when top-level status is ''{1}''.";

    public static final String MISSING_CACHEDUR_VALIDUNTIL = "Neither of Entity Descriptor's 'CacheDuration' & 'ValidUntil' attributes were provided. At least one must be provided.";

    public static final String MISSING_ORGANIZATION = "EntityDescriptor is missing 'Organization' element.";
    public static final String MISSING_DISPLAY_NAME = "EntityDescriptor is missing an organization 'DisplayName' element.";
    public static final String MISSING_DISP_NAME_LOCAL_STRING = "EntityDescriptor organization 'DisplayName' element is missing a localized string value.";


    public static final String MISSING_IDPSSODESCR = "EntityDescriptor is missing an 'IDPSSODescriptor' element.";
    public static final String MISSING_SUPPORTED_IDPSSODECR = "EntityDescriptor is missing an 'IDPSSODescriptor' element with a supported protocol.";
    public static final String MISSING_SSOS = "EntityDescriptor is missing a 'SingleSignOnService' element.";
    public static final String MISSING_SSOS_BINDING = "EntityDescriptor single sign-on service 'Binding' attribute is missing or has no value.";
    public static final String MISSING_SSOS_LOCATION = "EntityDescriptor single sign-on service 'Location' attribute is missing or has no value.";
    public static final String INVALID_REQUEST_ID = "RequestID must start with a letter or an underscore";


    public GenericHubProfileValidationSpecification(String errorFormat, Object... params) {
        super(MessageFormat.format(errorFormat, params), true);
    }

    @Override
    public SamlDocumentReference documentReference() {
        return SamlDocumentReference.hubProfile11a("");
    }
}

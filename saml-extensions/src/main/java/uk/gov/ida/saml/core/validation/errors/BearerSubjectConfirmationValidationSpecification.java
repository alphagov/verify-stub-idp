package uk.gov.ida.saml.core.validation.errors;

import uk.gov.ida.saml.core.validation.SamlDocumentReference;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;

import static java.text.MessageFormat.format;

public class BearerSubjectConfirmationValidationSpecification extends SamlValidationSpecificationFailure {

    public static final String IN_RESPONSE_TO_DOES_NOT_MATCH = "Bearer subject confirmation data''s ''InResponseTo'' attribute ({0}) was not the same as the Response''s ''InResponseTo'' ({1}).";
    public static final String INCORRECT_RECIPIENT_FORMAT = "''{0}'' - Bearer subject confirmation data ''Recipient'' attribute is not set to the expected value. Expected value was {1}";
    public static final String MISSING_SUBJECT_CONFIRMATION_DATA = "Bearer subject confirmation has no ''SubjectConfirmationData'' element.";
    public static final String NO_INRESPONSETO_VALUE = "Bearer subject confirmation data has no ''InResponseTo'' attribute.";
    public static final String NO_RECIPIENT = "Bearer subject confirmation data has no ''Recipient'' attribute.";
    public static final String NO_NOT_ON_OR_AFTER = "Bearer subject confirmation data has no ''NotOnOrAfter'' attribute.";
    public static final String EXCEEDED_NOT_ON_OR_AFTER = "Bearer subject confirmation data''s ''NotOnOrAfter'' timestamp of {0} has been exceeded.";
    public static final String NOT_BEFORE_ATTRIBUTE_EXISTS = "Bearer subject confirmation data contains a ''NotBefore'' attribute.";




    public BearerSubjectConfirmationValidationSpecification(String message, Object... parameters) {
        super(format(message, parameters), true);
    }

    @Override
    public SamlDocumentReference documentReference() {
        return SamlDocumentReference.hubProfile11a("2.1.4.2");
    }
}

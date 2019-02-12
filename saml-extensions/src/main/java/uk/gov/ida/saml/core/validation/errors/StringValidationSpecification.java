package uk.gov.ida.saml.core.validation.errors;

import uk.gov.ida.saml.core.validation.SamlDocumentReference;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;

import java.text.MessageFormat;

public class StringValidationSpecification extends SamlValidationSpecificationFailure {

    public static final String LOWER_BOUND_ERROR_MESSAGE = "The size of string is {0}; it should be at least {1}.";
    public static final String UPPER_BOUND_ERROR_MESSAGE = "The size of string is {0}; it should be less than or equal to {1}.";


    public StringValidationSpecification(String errorFormat, Object... params) {
        super(MessageFormat.format(errorFormat, params), false);
    }

    @Override
    public SamlDocumentReference documentReference() {
        return SamlDocumentReference.unspecified();
    }
}

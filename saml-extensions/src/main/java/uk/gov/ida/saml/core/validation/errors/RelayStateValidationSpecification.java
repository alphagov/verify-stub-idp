package uk.gov.ida.saml.core.validation.errors;

import uk.gov.ida.saml.core.validation.SamlDocumentReference;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;

import java.text.MessageFormat;

public class RelayStateValidationSpecification extends SamlValidationSpecificationFailure {

    public static final String INVALID_RELAY_STATE = "The RelayState has more than 80 characters: ''{0}''";
    public static final String INVALID_RELAY_STATE_CHARACTER = "The RelayState contains illegal character ''{0}'': ''{1}''";

    public RelayStateValidationSpecification(String errorFormat, Object... params) {
        super(MessageFormat.format(errorFormat, params), false);
    }

    @Override
    public SamlDocumentReference documentReference() {
        return SamlDocumentReference.samlBindings("3.5.3");
    }
}

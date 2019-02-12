package uk.gov.ida.saml.core.validation.errors;

import uk.gov.ida.saml.core.validation.SamlDocumentReference;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationWarning;

import static java.text.MessageFormat.format;

public class InvalidAttributeNameFormat extends SamlValidationSpecificationWarning {
    public InvalidAttributeNameFormat(String nameFormat) {
        super(format("''{0}'' is not a valid name format.", nameFormat), true);
    }

    @Override
    public SamlDocumentReference documentReference() {
        return SamlDocumentReference.idaAttributes11a("2.5");
    }
}

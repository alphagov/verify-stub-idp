package uk.gov.ida.saml.core.validation.errors;

import org.slf4j.event.Level;
import uk.gov.ida.saml.core.validation.SamlDocumentReference;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;

import static java.text.MessageFormat.format;

public class DuplicateRequestIdValidationSpecificationFailure extends SamlValidationSpecificationFailure {

    public static final String DUPLICATE_REQUEST_ID = "Duplicate request ID {0} from issuer {1}.";

    public DuplicateRequestIdValidationSpecificationFailure(String message, Object... params) {
        super(format(message, params), true, Level.WARN);
    }

    @Override
    public SamlDocumentReference documentReference() {
        return SamlDocumentReference.samlCore20("1.3.4");
    }
}

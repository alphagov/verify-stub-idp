package uk.gov.ida.saml.core.validation.errors;

import org.slf4j.event.Level;
import uk.gov.ida.saml.core.validation.SamlDocumentReference;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;

import java.text.MessageFormat;

public class RequestFreshnessValidationSpecification extends SamlValidationSpecificationFailure {

    public static final String REQUEST_TOO_OLD = "Request ID {0} too old (request issueInstant {1}, current time {2}).";

public RequestFreshnessValidationSpecification (String errorFormat, Object... params) {
    super(MessageFormat.format(errorFormat, params), false, Level.WARN);
}

    @Override
    public SamlDocumentReference documentReference() {
        return SamlDocumentReference.unspecified();
    }
}

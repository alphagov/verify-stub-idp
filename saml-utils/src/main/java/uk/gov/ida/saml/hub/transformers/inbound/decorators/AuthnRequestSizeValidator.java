package uk.gov.ida.saml.hub.transformers.inbound.decorators;

import uk.gov.ida.saml.deserializers.validators.SizeValidator;
import uk.gov.ida.saml.hub.validators.StringSizeValidator;

import javax.inject.Inject;

public class AuthnRequestSizeValidator implements SizeValidator {

    private static final int LOWER_BOUND = 1200;
    private static final int UPPER_BOUND = 6 * 1024;

    private final StringSizeValidator validator;

    @Inject
    public AuthnRequestSizeValidator(StringSizeValidator validator) {
        this.validator = validator;
    }

    @Override
    public void validate(String input) {
        validator.validate(input,LOWER_BOUND, UPPER_BOUND);
    }
}

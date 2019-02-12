package uk.gov.ida.saml.deserializers.validators;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import org.apache.commons.codec.binary.StringUtils;
import uk.gov.ida.saml.core.validation.SamlTransformationErrorException;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;

import static java.util.regex.Pattern.matches;
import static uk.gov.ida.saml.errors.SamlTransformationErrorFactory.invalidBase64Encoding;

public class Base64StringDecoder {

    public String decode(String input) {
        String withoutWhitespace = input.replaceAll("\\s", "");
        if (!matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$", withoutWhitespace)) {
            SamlValidationSpecificationFailure failure = invalidBase64Encoding(input);
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }

        return StringUtils.newStringUtf8(Base64Support.decode(input));
    }

}

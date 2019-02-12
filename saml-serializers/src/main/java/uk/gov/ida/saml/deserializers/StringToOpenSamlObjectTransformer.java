package uk.gov.ida.saml.deserializers;

import org.opensaml.core.xml.XMLObject;
import uk.gov.ida.saml.deserializers.validators.Base64StringDecoder;
import uk.gov.ida.saml.deserializers.validators.NotNullSamlStringValidator;
import uk.gov.ida.saml.deserializers.validators.SizeValidator;

import java.util.function.Function;

public class StringToOpenSamlObjectTransformer<TOutput extends XMLObject> implements Function<String, TOutput> {

    private final NotNullSamlStringValidator notNullSamlStringValidator;
    private final Base64StringDecoder base64StringDecoder;
    private final SizeValidator sizeValidator;
    private final OpenSamlXMLObjectUnmarshaller<TOutput> openSamlXMLObjectUnmarshaller;

    public StringToOpenSamlObjectTransformer(
            final NotNullSamlStringValidator notNullSamlStringValidator,
            final Base64StringDecoder base64StringDecoder,
            final SizeValidator sizeValidator,
            final OpenSamlXMLObjectUnmarshaller<TOutput> openSamlXMLObjectUnmarshaller) {

        this.notNullSamlStringValidator = notNullSamlStringValidator;
        this.base64StringDecoder = base64StringDecoder;
        this.sizeValidator = sizeValidator;
        this.openSamlXMLObjectUnmarshaller = openSamlXMLObjectUnmarshaller;
    }

    @Override
    public TOutput apply(final String input) {
        notNullSamlStringValidator.validate(input);
        final String decodedInput = base64StringDecoder.decode(input);
        sizeValidator.validate(decodedInput);
        return openSamlXMLObjectUnmarshaller.fromString(decodedInput);
    }

}

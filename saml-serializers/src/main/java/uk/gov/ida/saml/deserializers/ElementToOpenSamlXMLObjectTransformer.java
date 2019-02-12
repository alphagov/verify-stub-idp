package uk.gov.ida.saml.deserializers;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.validation.SamlTransformationErrorException;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;
import uk.gov.ida.saml.deserializers.parser.SamlObjectParser;

import java.util.function.Function;

import static uk.gov.ida.saml.errors.SamlTransformationErrorFactory.unableToUnmarshallElementToOpenSaml;

public class ElementToOpenSamlXMLObjectTransformer<TOutput extends XMLObject> implements Function<Element,TOutput> {
    private final SamlObjectParser samlObjectParser;

    public ElementToOpenSamlXMLObjectTransformer(SamlObjectParser samlObjectParser) {
        this.samlObjectParser = samlObjectParser;
    }

    @Override
    public TOutput apply(Element input) {
        try {
            return samlObjectParser.getSamlObject(input);
        } catch (UnmarshallingException e) {
            SamlValidationSpecificationFailure failure = unableToUnmarshallElementToOpenSaml(input.getLocalName());
            throw new SamlTransformationErrorException(failure.getErrorMessage(), e, failure.getLogLevel());
        }
    }

}

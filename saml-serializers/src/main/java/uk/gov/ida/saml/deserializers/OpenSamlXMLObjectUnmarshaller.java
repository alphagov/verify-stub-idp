package uk.gov.ida.saml.deserializers;

import net.shibboleth.utilities.java.support.xml.XMLParserException;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import uk.gov.ida.saml.core.validation.SamlTransformationErrorException;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;
import uk.gov.ida.saml.deserializers.parser.SamlObjectParser;

import static uk.gov.ida.saml.errors.SamlTransformationErrorFactory.unableToDeserializeStringToOpenSaml;

public class OpenSamlXMLObjectUnmarshaller<TOutput extends XMLObject> {

    private final SamlObjectParser samlObjectParser;

    public OpenSamlXMLObjectUnmarshaller(SamlObjectParser samlObjectParser) {
        this.samlObjectParser = samlObjectParser;
    }

    public TOutput fromString(String input) {
        try {
            return samlObjectParser.getSamlObject(input);
        } catch (UnmarshallingException | XMLParserException  e) {
            SamlValidationSpecificationFailure failure = unableToDeserializeStringToOpenSaml(input);
            throw new SamlTransformationErrorException(failure.getErrorMessage(), e, failure.getLogLevel());
        }
    }
}

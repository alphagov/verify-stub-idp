package uk.gov.ida.saml.security.saml.deserializers;

import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.xml.sax.SAXException;
import uk.gov.ida.saml.core.validation.SamlTransformationErrorException;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static uk.gov.ida.saml.security.errors.SamlTransformationErrorFactory.unableToDeserializeStringToOpenSaml;

public class AuthnRequestUnmarshaller {

    private final SamlObjectParser samlObjectParser;

    public AuthnRequestUnmarshaller(SamlObjectParser samlObjectParser) {
        this.samlObjectParser = samlObjectParser;
    }

    public AuthnRequest fromString(String input) {
        try {
            return samlObjectParser.getSamlObject(input);
        } catch (ParserConfigurationException | SAXException | IOException | UnmarshallingException e) {
            SamlValidationSpecificationFailure failure = unableToDeserializeStringToOpenSaml(input);
            throw new SamlTransformationErrorException(failure.getErrorMessage(), e, failure.getLogLevel());
        }
    }
}

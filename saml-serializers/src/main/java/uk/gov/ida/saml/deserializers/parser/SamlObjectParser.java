package uk.gov.ida.saml.deserializers.parser;

import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.XMLParserException;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class SamlObjectParser {

    @SuppressWarnings("unchecked")
    public <T extends XMLObject> T getSamlObject(String xmlString) throws UnmarshallingException, XMLParserException {
        ParserPool parserPool = XMLObjectProviderRegistrySupport.getParserPool();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
        return (T) XMLObjectSupport.unmarshallFromInputStream(parserPool, inputStream);
    }

    @SuppressWarnings("unchecked")
    public <T extends XMLObject> T getSamlObject(Element samlRootElement) throws UnmarshallingException {
        // Get appropriate unmarshaller
        UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(samlRootElement);

        // Unmarshall using the document root element
        return (T) unmarshaller.unmarshall(samlRootElement);
    }
}

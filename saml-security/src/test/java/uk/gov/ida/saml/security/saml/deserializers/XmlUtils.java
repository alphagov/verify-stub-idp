package uk.gov.ida.saml.security.saml.deserializers;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING;

public abstract class XmlUtils {
    private static final String FEATURE_DISALLOW_DOCTYPE_DECLARATIONS =
            "http://apache.org/xml/features/disallow-doctype-decl";

    public static Element convertToElement(String xmlString) throws ParserConfigurationException, SAXException, IOException {
        return newDocumentBuilder().parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8))).getDocumentElement();
    }

    public static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(FEATURE_SECURE_PROCESSING, true);
        factory.setFeature(FEATURE_DISALLOW_DOCTYPE_DECLARATIONS, true);
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder();
    }
}

package uk.gov.ida.shared.utils.xml;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static com.google.common.base.Throwables.propagate;
import static javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING;

/**
 * Due to security requirements, {@link javax.xml.parsers.DocumentBuilder} and
 * {@link javax.xml.parsers.DocumentBuilderFactory} should *only* be used via
 * the utility methods in this class.  For more information on the vulnerabilities
 * identified, see the tests.
 * @see uk.gov.ida.shared.utils.xml.XmlUtilsTest
 */
public abstract class XmlUtils {
    private static final String FEATURE_DISALLOW_DOCTYPE_DECLARATIONS =
            "http://apache.org/xml/features/disallow-doctype-decl";

    private static final Logger LOG = LoggerFactory.getLogger(XmlUtils.class);

    public static String writeToString(Node node) {
        try {
            StringWriter docWriter = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(node), new StreamResult(docWriter));
            return docWriter.toString();
        } catch (TransformerException ex) {
            LOG.error("Unable to convert Element to String", ex);
            throw Throwables.propagate(ex);
        }
    }

    public static Element convertToElement(String xmlString) throws ParserConfigurationException, SAXException, IOException {
        return newDocumentBuilder().parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8))).getDocumentElement();
    }

    public static Document convertToDocument(String xmlString) {
        try {
            return newDocumentBuilder().parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw propagate(e);
        }
    }

    /**
     * @return a namespace-aware document builder
     */
    public static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // Enable secure processing.  For more details, see https://jaxp.java.net/1.4/JAXP-Compatibility.html#JAXP_security
        factory.setFeature(FEATURE_SECURE_PROCESSING, true);
        factory.setFeature(FEATURE_DISALLOW_DOCTYPE_DECLARATIONS, true);
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder();
    }
}

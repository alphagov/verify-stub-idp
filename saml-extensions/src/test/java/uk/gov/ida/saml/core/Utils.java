package uk.gov.ida.saml.core;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import net.shibboleth.utilities.java.support.xml.XMLParserException;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.SAMLObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.StringReader;

public class Utils {
    @SuppressWarnings("unchecked")
    public static <T extends SAMLObject> T unmarshall(String assertion) throws XMLParserException, UnmarshallingException, ComponentInitializationException {
        // Get parser pool manager
        BasicParserPool ppMgr = new BasicParserPool();
        ppMgr.setNamespaceAware(true);
        ppMgr.initialize();

        // Parse metadata file
        Document doc = ppMgr.parse(new StringReader(assertion));
        Element rootElement = doc.getDocumentElement();

        Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(rootElement);
        return (T) unmarshaller.unmarshall(rootElement);
    }
}

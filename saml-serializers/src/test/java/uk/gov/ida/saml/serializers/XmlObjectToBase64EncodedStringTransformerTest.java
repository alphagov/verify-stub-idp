package uk.gov.ida.saml.serializers;

import org.apache.commons.codec.binary.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.impl.AuthnRequestBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

public class XmlObjectToBase64EncodedStringTransformerTest {

    private XmlObjectToBase64EncodedStringTransformer xmlObjectToBase64EncodedStringTransformer;

    @Before
    public void setup() throws InitializationException {
        InitializationService.initialize();
        xmlObjectToBase64EncodedStringTransformer = new XmlObjectToBase64EncodedStringTransformer();
    }

    @Test
    public void shouldTransformAuthnRequestToBase64EncodedString() throws ParserConfigurationException, IOException, SAXException {
        AuthnRequest authnRequest = new AuthnRequestBuilder().buildObject();
        String encodedString = xmlObjectToBase64EncodedStringTransformer.apply(authnRequest);

        Document doc = convertEncodedXmlStringToDoc(encodedString);
        NamedNodeMap xmlAttributes = doc.getElementsByTagName("saml2p:AuthnRequest").item(0).getAttributes();
        String version = xmlAttributes.getNamedItem("Version").toString();
        String saml2p = xmlAttributes.getNamedItem("xmlns:saml2p").toString();

        assertThat(doc.getXmlVersion()).isEqualTo("1.0");
        assertThat(version).isEqualTo("Version=\"2.0\"");
        assertThat(saml2p).isEqualTo("xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\"");
    }

    private Document convertEncodedXmlStringToDoc(String encodedString) throws IOException, SAXException, ParserConfigurationException {
        String decodedString = StringUtils.newStringUtf8(Base64.getDecoder().decode(StringUtils.getBytesUtf8(encodedString)));
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource src = new InputSource();
        src.setCharacterStream(new StringReader(decodedString));

        return builder.parse(src);
    }
}

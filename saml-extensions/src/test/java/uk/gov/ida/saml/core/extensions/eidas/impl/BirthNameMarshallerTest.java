package uk.gov.ida.saml.core.extensions.eidas.impl;

import net.shibboleth.utilities.java.support.xml.XMLConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.extensions.eidas.BirthName;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.core.IdaConstants.EIDAS_NATURUAL_PREFIX;

@RunWith(OpenSAMLRunner.class)
public class BirthNameMarshallerTest {

    @Test
    public void shouldMarshallBirthName() throws Exception {
        final String fullName = "Sarah Jane Booth";
        final BirthName birthName = new BirthNameBuilder().buildObject();
        final Marshaller birthNameMarshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(birthName);
        birthName.setBirthName(fullName);

        final Element marshalledElement = birthNameMarshaller.marshall(birthName);

        assertThat(marshalledElement.getNamespaceURI()).isEqualTo(BirthName.DEFAULT_ELEMENT_NAME.getNamespaceURI());
        assertThat(marshalledElement.getAttributeNS(XMLConstants.XSI_NS, XMLConstants.XSI_TYPE_ATTRIB_NAME.getLocalPart())).isEqualTo(String.format("%s:%s", EIDAS_NATURUAL_PREFIX, BirthName.TYPE_LOCAL_NAME));
        assertThat(marshalledElement.getTextContent()).isEqualTo(fullName);
    }
}

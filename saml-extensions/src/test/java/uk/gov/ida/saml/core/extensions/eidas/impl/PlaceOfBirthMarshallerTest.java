package uk.gov.ida.saml.core.extensions.eidas.impl;

import net.shibboleth.utilities.java.support.xml.XMLConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.extensions.eidas.PlaceOfBirth;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.core.IdaConstants.EIDAS_NATURUAL_PREFIX;

@RunWith(OpenSAMLRunner.class)
public class PlaceOfBirthMarshallerTest {

    @Test
    public void shouldMarshallPlaceOfBirth() throws Exception {
        final String place = "Peterborough";
        final PlaceOfBirth placeOfBirth = new PlaceOfBirthBuilder().buildObject();
        final Marshaller placeOfBirthMarshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(placeOfBirth);
        placeOfBirth.setPlaceOfBirth(place);

        final Element marshalledElement = placeOfBirthMarshaller.marshall(placeOfBirth);

        assertThat(marshalledElement.getNamespaceURI()).isEqualTo(PlaceOfBirth.DEFAULT_ELEMENT_NAME.getNamespaceURI());
        assertThat(marshalledElement.getAttributeNS(XMLConstants.XSI_NS, XMLConstants.XSI_TYPE_ATTRIB_NAME.getLocalPart())).isEqualTo(String.format("%s:%s", EIDAS_NATURUAL_PREFIX, PlaceOfBirth.TYPE_LOCAL_NAME));
        assertThat(marshalledElement.getTextContent()).isEqualTo(place);
    }
}

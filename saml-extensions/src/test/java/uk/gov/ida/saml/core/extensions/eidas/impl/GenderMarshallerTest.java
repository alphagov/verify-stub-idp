package uk.gov.ida.saml.core.extensions.eidas.impl;

import net.shibboleth.utilities.java.support.xml.XMLConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.extensions.eidas.Gender;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.core.IdaConstants.EIDAS_NATURUAL_PREFIX;

@RunWith(OpenSAMLRunner.class)
public class GenderMarshallerTest {

    @Test
    public void shouldMarshallGender() throws Exception {
        final String genderValue = "Male";
        final Gender gender = new GenderBuilder().buildObject();
        final Marshaller genderMarshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(gender);
        gender.setValue(genderValue);

        final Element marshalledElement = genderMarshaller.marshall(gender);

        assertThat(marshalledElement.getNamespaceURI()).isEqualTo(Gender.DEFAULT_ELEMENT_NAME.getNamespaceURI());
        assertThat(marshalledElement.getAttributeNS(XMLConstants.XSI_NS, XMLConstants.XSI_TYPE_ATTRIB_NAME.getLocalPart())).isEqualTo(String.format("%s:%s", EIDAS_NATURUAL_PREFIX, Gender.TYPE_LOCAL_NAME));
        assertThat(marshalledElement.getTextContent()).isEqualTo(genderValue);
    }
}

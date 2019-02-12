package uk.gov.ida.saml.core.extensions.eidas.impl;

import net.shibboleth.utilities.java.support.xml.XMLConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.extensions.eidas.CurrentFamilyName;
import uk.gov.ida.saml.core.extensions.eidas.TransliterableString;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.core.IdaConstants.EIDAS_NATURUAL_PREFIX;

@RunWith(OpenSAMLRunner.class)
public class CurrentFamilyNameMarshallerTest {

    private CurrentFamilyName currentFamilyName;
    private Marshaller currentFamilyNameMarshaller;

    @Before
    public void setUp() {
        currentFamilyName = new CurrentFamilyNameBuilder().buildObject();
        currentFamilyNameMarshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(currentFamilyName);
    }

    @Test
    public void shouldMarshallCurrentFamilyName() throws Exception {
        final String familyName = "Garcia";
        currentFamilyName.setFamilyName(familyName);

        final Element marshalledElement = currentFamilyNameMarshaller.marshall(currentFamilyName);

        assertThat(marshalledElement.getNamespaceURI()).isEqualTo(CurrentFamilyName.DEFAULT_ELEMENT_NAME.getNamespaceURI());
        assertThat(marshalledElement.getAttributeNS(XMLConstants.XSI_NS, XMLConstants.XSI_TYPE_ATTRIB_NAME.getLocalPart())).isEqualTo(String.format("%s:%s", EIDAS_NATURUAL_PREFIX, CurrentFamilyName.TYPE_LOCAL_NAME));
        assertThat(marshalledElement.getTextContent()).isEqualTo(familyName);
    }

    @Test
    public void shouldMarshallWhenIsLatinScriptIsTrue() throws Exception {
        currentFamilyName.setIsLatinScript(true);

        final Element marshalledElement = currentFamilyNameMarshaller.marshall(currentFamilyName);

        assertThat(marshalledElement.getAttribute(TransliterableString.IS_LATIN_SCRIPT_ATTRIBUTE_NAME)).isEqualTo("");
    }

    @Test
    public void shouldMarshallWhenIsLatinScriptIsFalse() throws Exception {
        currentFamilyName.setIsLatinScript(false);

        final Element marshalledElement = currentFamilyNameMarshaller.marshall(currentFamilyName);

        assertThat(marshalledElement.getAttribute(TransliterableString.IS_LATIN_SCRIPT_ATTRIBUTE_NAME)).isEqualTo("false");
    }
}

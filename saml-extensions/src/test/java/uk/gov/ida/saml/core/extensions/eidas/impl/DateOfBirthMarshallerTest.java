package uk.gov.ida.saml.core.extensions.eidas.impl;

import net.shibboleth.utilities.java.support.xml.XMLConstants;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.extensions.eidas.DateOfBirth;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.core.IdaConstants.EIDAS_NATURUAL_PREFIX;
import static uk.gov.ida.saml.core.extensions.eidas.impl.DateOfBirthImpl.DATE_OF_BIRTH_FORMAT;

@RunWith(OpenSAMLRunner.class)
public class DateOfBirthMarshallerTest {

    @Test
    public void shouldMarshallDateOfBirth() throws Exception {
        final LocalDate date = LocalDate.parse("1965-01-01", DATE_OF_BIRTH_FORMAT);
        final DateOfBirth dateOfBirth = new DateOfBirthBuilder().buildObject();
        final Marshaller dateOfBirthMarshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(dateOfBirth);
        dateOfBirth.setDateOfBirth(date);

        final Element marshalledElement = dateOfBirthMarshaller.marshall(dateOfBirth);

        assertThat(marshalledElement.getNamespaceURI()).isEqualTo(DateOfBirth.DEFAULT_ELEMENT_NAME.getNamespaceURI());
        assertThat(marshalledElement.getAttributeNS(XMLConstants.XSI_NS, XMLConstants.XSI_TYPE_ATTRIB_NAME.getLocalPart())).isEqualTo(String.format("%s:%s", EIDAS_NATURUAL_PREFIX, DateOfBirth.TYPE_LOCAL_NAME));
        assertThat(marshalledElement.getTextContent()).isEqualTo(date.toString());
    }
}

package uk.gov.ida.saml.core.extensions.impl;

import net.shibboleth.utilities.java.support.xml.XMLConstants;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.extensions.Date;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.core.IdaConstants.IDA_NS;
import static uk.gov.ida.saml.core.IdaConstants.IDA_PREFIX;

@RunWith(OpenSAMLRunner.class)
public class DateMarshallerTest {

    private Marshaller marshaller;
    private Date date;

    @Before
    public void setUp() throws Exception {
        date = new DateBuilder().buildObject();
        marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(date);
    }

    @Test
    public void marshall_shouldMarshallDateTime() throws Exception {
        String datetimeValue = new org.joda.time.DateTime(1984, 2, 28, 0, 0).toString();
        date.setValue(datetimeValue);

        Element marshalledElement = marshaller.marshall(date);

        assertThat(marshalledElement.getNamespaceURI()).isEqualTo(Date.DEFAULT_ELEMENT_NAME.getNamespaceURI());

        assertThat(marshalledElement.getAttributeNS(XMLConstants.XSI_NS, XMLConstants.XSI_TYPE_ATTRIB_NAME.getLocalPart())).isEqualTo(String.format("%s:%s", IDA_PREFIX, Date.TYPE_LOCAL_NAME));
        assertThat(marshalledElement.getTextContent()).isEqualTo(datetimeValue);
    }

    @Test
    public void marshall_shouldMarshallFromDateInCorrectFormat() throws Exception {
        String fromDate = "2012-02-09";
        date.setFrom(DateTime.parse(fromDate));

        Element marshalledElement = marshaller.marshall(date);

        assertThat(marshalledElement.getAttributeNodeNS(IDA_NS, Date.FROM_ATTRIB_NAME).getValue()).isEqualTo(fromDate);
    }

    @Test
    public void marshall_shouldMarshallFromDateWithNamespacePrefix() throws Exception {
        date.setFrom(DateTime.parse("2012-02-09"));

        Element marshalledElement = marshaller.marshall(date);

        assertThat(marshalledElement.getAttributeNodeNS(IDA_NS, Date.FROM_ATTRIB_NAME).getPrefix()).isEqualTo(IdaConstants.IDA_PREFIX);
    }

    @Test
    public void marshall_shouldMarshallToDateInCorrectFormat() throws Exception {
        String toDate = "2012-02-09";
        date.setTo(DateTime.parse(toDate));

        Element marshalledElement = marshaller.marshall(date);

        assertThat(marshalledElement.getAttributeNodeNS(IDA_NS, Date.TO_ATTRIB_NAME).getValue()).isEqualTo(toDate);
    }

    @Test
    public void marshall_shouldMarshallToDateWithNamespacePrefix() throws Exception {
        date.setTo(DateTime.parse("2012-02-09"));

        Element marshalledElement = marshaller.marshall(date);

        assertThat(marshalledElement.getAttributeNodeNS(IDA_NS, Date.TO_ATTRIB_NAME).getPrefix()).isEqualTo(IDA_PREFIX);
    }

    @Test
    public void marshall_shouldMarshallVerifiedWhenTrue() throws Exception {
        checkMarshallingVerifiedAttributeWithValue(true);
    }

    @Test
    public void marshall_shouldMarshallVerifiedWhenFalse() throws Exception {
        checkMarshallingVerifiedAttributeWithValue(false);
    }

    @Test
    public void marshall_shouldEnsureXsiNamespaceDefinitionIsInluded() throws Exception {
        Element marshalledElement = marshaller.marshall(new DateBuilder().buildObject());

        assertThat(marshalledElement.hasAttributeNS(XMLConstants.XMLNS_NS, XMLConstants.XSI_PREFIX)).isTrue();
    }

    @Test
    public void marshall_shouldMarshallVerifiedWithPrefix() throws MarshallingException {
        date.setVerified(true);

        Element marshalledElement = marshaller.marshall(date);

        assertThat(marshalledElement.getAttributeNodeNS(IDA_NS, Date.VERIFIED_ATTRIB_NAME).getPrefix()).isEqualTo(IDA_PREFIX);
    }

    private void checkMarshallingVerifiedAttributeWithValue(boolean verifiedValue) throws MarshallingException {
        date.setVerified(verifiedValue);

        Element marshalledElement = marshaller.marshall(date);

        assertThat(Boolean.parseBoolean(marshalledElement.getAttributeNodeNS(IDA_NS, Date.VERIFIED_ATTRIB_NAME).getValue())).isEqualTo(verifiedValue);
    }
}

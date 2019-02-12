package uk.gov.ida.saml.core.extensions.impl;

import net.shibboleth.utilities.java.support.xml.XMLConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.extensions.PersonName;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.core.IdaConstants.IDA_NS;
import static uk.gov.ida.saml.core.IdaConstants.IDA_PREFIX;

@RunWith(OpenSAMLRunner.class)
public class PersonNameMarshallerTest {

    private Marshaller marshaller;
    private PersonName personName;

    @Before
    public void setUp() throws Exception {
        personName = new PersonNameBuilder().buildObject();
        marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(personName);
    }

    @Test
    public void marshall_shouldMarshallPersonName() throws Exception {
        String name = "John";
        String language = "en-GB";
        personName.setValue(name);
        personName.setLanguage(language);

        Element marshalledElement = marshaller.marshall(personName);

        assertThat(marshalledElement.getNamespaceURI()).isEqualTo(PersonName.DEFAULT_ELEMENT_NAME.getNamespaceURI());

        assertThat(marshalledElement.getAttributeNS(XMLConstants.XSI_NS, XMLConstants.XSI_TYPE_ATTRIB_NAME.getLocalPart())).isEqualTo(String.format("%s:%s", IDA_PREFIX, PersonName.TYPE_LOCAL_NAME));
        assertThat(marshalledElement.getAttributeNS(IdaConstants.IDA_NS, PersonName.LANGUAGE_ATTRIB_NAME)).isEqualTo(language);
        assertThat(marshalledElement.getTextContent()).isEqualTo(name);
    }

    @Test
    public void marshall_shouldEnsureXsiNamespaceDefinitionIsInluded() throws Exception {
        Element marshalledElement = marshaller.marshall(new PersonNameBuilder().buildObject());

        assertThat(marshalledElement.hasAttributeNS(XMLConstants.XMLNS_NS, XMLConstants.XSI_PREFIX)).isTrue();
    }

    @Test
    public void marshall_shouldMarshallFromDateInCorrectFormat() throws Exception {
        String fromDate = "2012-02-09";
        personName.setFrom(org.joda.time.DateTime.parse(fromDate));

        Element marshalledElement = marshaller.marshall(personName);

        assertThat(marshalledElement.getAttributeNodeNS(IDA_NS, PersonName.FROM_ATTRIB_NAME).getValue()).isEqualTo(fromDate);
    }

    @Test
    public void marshall_shouldMarshallFromDateWithNamespacePrefix() throws Exception {
        personName.setFrom(org.joda.time.DateTime.parse("2012-02-09"));

        Element marshalledElement = marshaller.marshall(personName);

        assertThat(marshalledElement.getAttributeNodeNS(IDA_NS, PersonName.FROM_ATTRIB_NAME).getPrefix()).isEqualTo(IdaConstants.IDA_PREFIX);
    }

    @Test
    public void marshall_shouldMarshallToDateInCorrectFormat() throws Exception {
        String toDate = "2012-02-09";
        personName.setTo(org.joda.time.DateTime.parse(toDate));

        Element marshalledElement = marshaller.marshall(personName);

        assertThat(marshalledElement.getAttributeNodeNS(IDA_NS, PersonName.TO_ATTRIB_NAME).getValue()).isEqualTo(toDate);
    }

    @Test
    public void marshall_shouldMarshallToDateWithNamespacePrefix() throws Exception {
        personName.setTo(org.joda.time.DateTime.parse("2012-02-09"));

        Element marshalledElement = marshaller.marshall(personName);

        assertThat(marshalledElement.getAttributeNodeNS(IDA_NS, PersonName.TO_ATTRIB_NAME).getPrefix()).isEqualTo(IdaConstants.IDA_PREFIX);
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
    public void marshall_shouldMarshallVerifiedWithNamespacePrefix() throws Exception {
        personName.setVerified(true);

        Element marshalledElement = marshaller.marshall(personName);

        assertThat(marshalledElement.getAttributeNodeNS(IDA_NS, PersonName.VERIFIED_ATTRIB_NAME).getPrefix()).isEqualTo(IdaConstants.IDA_PREFIX);
    }

    private void checkMarshallingVerifiedAttributeWithValue(boolean verifiedValue) throws MarshallingException {
        personName.setVerified(verifiedValue);

        Element marshalledElement = marshaller.marshall(personName);

        assertThat(Boolean.parseBoolean(marshalledElement.getAttributeNodeNS(IDA_NS, PersonName.VERIFIED_ATTRIB_NAME).getValue())).isEqualTo(verifiedValue);
    }
}

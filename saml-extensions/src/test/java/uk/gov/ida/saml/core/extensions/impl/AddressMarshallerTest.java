package uk.gov.ida.saml.core.extensions.impl;

import net.shibboleth.utilities.java.support.xml.XMLConstants;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.ida.saml.core.extensions.Address;
import uk.gov.ida.saml.core.extensions.InternationalPostCode;
import uk.gov.ida.saml.core.extensions.Line;
import uk.gov.ida.saml.core.extensions.PostCode;
import uk.gov.ida.saml.core.extensions.UPRN;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static uk.gov.ida.saml.core.IdaConstants.IDA_NS;
import static uk.gov.ida.saml.core.IdaConstants.IDA_PREFIX;

@RunWith(OpenSAMLRunner.class)
public class AddressMarshallerTest {

    private static final String FROM_DATE = "2011-10-20";
    private static final String TO_DATE = "2012-12-20";
    private static final String LINE_1_VALUE = "line 1";
    private static final String LINE_2_VALUE = "line 2";
    private static final String POST_CODE_VALUE = "RG99 1YY";
    private static final String INTERNATIONAL_POST_CODE_VALUE = "RG99 1YY";
    private static final String UPRN_VALUE = "RG99 1YY";
    private Marshaller marshaller;
    private Address address;

    @Before
    public void before() {
        address = createAddress();
        marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(address);
        assertNotNull(marshaller);
    }

    @Test
    public void marshall_shouldMarshallAddress() throws Exception {
        Element addressElement = marshaller.marshall(address);

        assertThat(addressElement.getNamespaceURI()).isEqualTo(Address.DEFAULT_ELEMENT_NAME.getNamespaceURI());

        assertThat(addressElement.getAttributeNS(XMLConstants.XSI_NS, XMLConstants.XSI_TYPE_ATTRIB_NAME.getLocalPart())).isEqualTo(String.format("%s:%s", IDA_PREFIX, Address.TYPE_LOCAL_NAME));

        NodeList lineNodeList = addressElement.getElementsByTagNameNS(IDA_NS, Line.DEFAULT_ELEMENT_LOCAL_NAME);
        assertThat(lineNodeList.getLength()).isEqualTo(2);
        Node line1Node = lineNodeList.item(0);
        assertThat(line1Node.getTextContent()).isEqualTo(LINE_1_VALUE);
        Node line2Node = lineNodeList.item(1);
        assertThat(line2Node.getTextContent()).isEqualTo(LINE_2_VALUE);

        assertThat(addressElement.getElementsByTagNameNS(IDA_NS, PostCode.DEFAULT_ELEMENT_LOCAL_NAME).getLength()).isEqualTo(1);
        Node postCodeNode = addressElement.getElementsByTagNameNS(IDA_NS, PostCode.DEFAULT_ELEMENT_LOCAL_NAME).item(0);
        assertThat(postCodeNode.getTextContent()).isEqualTo(POST_CODE_VALUE);

        assertThat(addressElement.getElementsByTagNameNS(IDA_NS, InternationalPostCode.DEFAULT_ELEMENT_LOCAL_NAME).getLength()).isEqualTo(1);
        Node internationalPostCodeNode = addressElement.getElementsByTagNameNS(IDA_NS, InternationalPostCode.DEFAULT_ELEMENT_LOCAL_NAME).item(0);
        assertThat(internationalPostCodeNode.getTextContent()).isEqualTo(INTERNATIONAL_POST_CODE_VALUE);

        assertThat(addressElement.getElementsByTagNameNS(IDA_NS, UPRN.DEFAULT_ELEMENT_LOCAL_NAME).getLength()).isEqualTo(1);
        Node uprnNode = addressElement.getElementsByTagNameNS(IDA_NS, UPRN.DEFAULT_ELEMENT_LOCAL_NAME).item(0);
        assertThat(uprnNode.getTextContent()).isEqualTo(UPRN_VALUE);
    }

    @Test
    public void marshall_shouldNotAddFromDateAttributeWhenNoFromDateIsSet() throws Exception {
        address.setFrom(null);
        Element addressElement = marshaller.marshall(address);

        assertThat(addressElement.hasAttributeNS(IDA_NS, Address.FROM_ATTRIB_NAME)).isFalse();
    }

    @Test
    public void marshall_shouldNotAddToDateAttributeWhenNoToDateIsSet() throws Exception {
        address.setTo(null);
        Element addressElement = marshaller.marshall(address);

        assertThat(addressElement.hasAttributeNS(IDA_NS, Address.TO_ATTRIB_NAME)).isFalse();
    }

    @Test
    public void marshall_shouldEnsureXsiNamespaceDefinitionIsIncluded() throws Exception {
        Element marshalledElement = marshaller.marshall(new AddressBuilder().buildObject());

        assertThat(marshalledElement.hasAttributeNS(XMLConstants.XMLNS_NS, XMLConstants.XSI_PREFIX)).isTrue();
    }

    @Test
    public void marshall_shouldMarshallFromDateInCorrectFormat() throws Exception {
        String fromDate = "2012-02-09";
        address.setFrom(DateTime.parse(fromDate));

        Element marshalledElement = marshaller.marshall(address);

        assertThat(marshalledElement.getAttributeNodeNS(IDA_NS, Address.FROM_ATTRIB_NAME).getValue()).isEqualTo(fromDate);
    }

    @Test
    public void marshall_shouldMarshallFromDateWithNamespacePrefix() throws Exception {
        address.setFrom(DateTime.parse("2012-02-09"));

        Element marshalledElement = marshaller.marshall(address);

        assertThat(marshalledElement.getAttributeNodeNS(IDA_NS, Address.FROM_ATTRIB_NAME).getPrefix()).isEqualTo(IDA_PREFIX);
    }

    @Test
    public void marshall_shouldMarshallToDateInCorrectFormat() throws Exception {
        String toDate = "2012-02-09";
        address.setTo(DateTime.parse(toDate));

        Element marshalledElement = marshaller.marshall(address);

        assertThat(marshalledElement.getAttributeNodeNS(IDA_NS, Address.TO_ATTRIB_NAME).getValue()).isEqualTo(toDate);
    }

    @Test
    public void marshall_shouldMarshallToDateWithNamespacePrefix() throws Exception {
        address.setTo(DateTime.parse("2012-02-09"));

        Element marshalledElement = marshaller.marshall(address);

        assertThat(marshalledElement.getAttributeNodeNS(IDA_NS, Address.TO_ATTRIB_NAME).getPrefix()).isEqualTo(IDA_PREFIX);
    }

    @Test
    public void marshall_shouldMarshallVerifiedWhenFalse() throws Exception {
        checkMarshallingVerifiedAttributeWithValue(false);
    }

    @Test
    public void marshall_shouldMarshallVerifiedWhenTrue() throws Exception {
        checkMarshallingVerifiedAttributeWithValue(true);
    }

    @Test
    public void marshall_shouldMarshallVerifiedWithNamespacePrefix() throws MarshallingException {
        address.setVerified(true);

        Element marshalledElement = marshaller.marshall(address);

        assertThat(marshalledElement.getAttributeNodeNS(IDA_NS, Address.VERIFIED_ATTRIB_NAME).getPrefix()).isEqualTo(IDA_PREFIX);
    }

    private void checkMarshallingVerifiedAttributeWithValue(boolean verified) throws MarshallingException {
        address.setVerified(verified);

        Element marshalledElement = marshaller.marshall(address);

        assertThat(Boolean.parseBoolean(marshalledElement.getAttributeNodeNS(IDA_NS, Address.VERIFIED_ATTRIB_NAME).getValue())).isEqualTo(verified);
    }

    @Test
    public void marshall_shouldNotAddPostCodeElementWhenNotSet() throws Exception {
        address.setPostCode(null);

        Element addressElement = marshaller.marshall(address);

        final NodeList postCodeElements = addressElement.getElementsByTagNameNS(IDA_NS, PostCode.DEFAULT_ELEMENT_LOCAL_NAME);
        assertThat(postCodeElements.getLength()).isEqualTo(0);
    }

    @Test
    public void marshall_shouldNotAddInternationalPostCodeElementWhenNotSet() throws Exception {
        address.setInternationalPostCode(null);

        Element addressElement = marshaller.marshall(address);

        final NodeList internationalPostCodeElements = addressElement.getElementsByTagNameNS(IDA_NS, InternationalPostCode.DEFAULT_ELEMENT_LOCAL_NAME);
        assertThat(internationalPostCodeElements.getLength()).isEqualTo(0);
    }

    @Test
    public void marshall_shouldNotAddUPRNElementWhenNotSet() throws Exception {
        address.setUPRN(null);

        Element addressElement = marshaller.marshall(address);

        final NodeList uprnElements = addressElement.getElementsByTagNameNS(IDA_NS, UPRN.DEFAULT_ELEMENT_LOCAL_NAME);
        assertThat(uprnElements.getLength()).isEqualTo(0);
    }

    private Address createAddress() {
        Address address = new AddressImpl(SAMLConstants.SAML20_NS, Address.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        address.setFrom(DateTime.parse(FROM_DATE, DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.UTC)));
        address.setTo(DateTime.parse(TO_DATE, DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.UTC)));

        PostCode postCode = new PostCodeImpl(IDA_NS, PostCode.DEFAULT_ELEMENT_LOCAL_NAME, IDA_PREFIX);
        postCode.setValue(POST_CODE_VALUE);
        address.setPostCode(postCode);

        InternationalPostCode internationalPostCode = new InternationalPostCodeImpl(IDA_NS, InternationalPostCode.DEFAULT_ELEMENT_LOCAL_NAME, IDA_PREFIX);
        internationalPostCode.setValue(POST_CODE_VALUE);
        address.setInternationalPostCode(internationalPostCode);

        UPRN uprn = new UPRNImpl(IDA_NS, UPRN.DEFAULT_ELEMENT_LOCAL_NAME, IDA_PREFIX);
        uprn.setValue(POST_CODE_VALUE);
        address.setUPRN(uprn);

        Line line1 = new LineImpl(IDA_NS, Line.DEFAULT_ELEMENT_LOCAL_NAME, IDA_PREFIX);
        line1.setValue(LINE_1_VALUE);
        address.getLines().add(line1);

        Line line2 = new LineImpl(IDA_NS, Line.DEFAULT_ELEMENT_LOCAL_NAME, IDA_PREFIX);
        line2.setValue(LINE_2_VALUE);
        address.getLines().add(line2);
        return address;
    }
}

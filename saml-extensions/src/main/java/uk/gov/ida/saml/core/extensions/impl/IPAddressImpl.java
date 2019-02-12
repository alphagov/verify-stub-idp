package uk.gov.ida.saml.core.extensions.impl;

import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.Unmarshaller;
import uk.gov.ida.saml.core.extensions.IPAddress;

public class IPAddressImpl extends StringBasedMdsAttributeValueImpl implements IPAddress {
    public static final Marshaller MARSHALLER = new StringBasedMdsAttributeValueMarshaller(IPAddress.TYPE_LOCAL_NAME);
    public static final Unmarshaller UNMARSHALLER = new StringBasedMdsAttributeValueUnmarshaller();

    protected IPAddressImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
}

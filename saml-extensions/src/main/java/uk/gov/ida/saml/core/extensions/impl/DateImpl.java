package uk.gov.ida.saml.core.extensions.impl;

import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.Unmarshaller;
import uk.gov.ida.saml.core.extensions.Date;

import javax.xml.namespace.QName;

public class DateImpl extends StringBasedMdsAttributeValueImpl implements Date {
    public static final Marshaller MARSHALLER = new StringBasedMdsAttributeValueMarshaller(Date.TYPE_LOCAL_NAME);
    public static final Unmarshaller UNMARSHALLER = new StringBasedMdsAttributeValueUnmarshaller();

    protected DateImpl(String namespaceURI, String localName, String namespacePrefix) {
        this(namespaceURI, localName, namespacePrefix, Date.TYPE_NAME);
    }

    protected DateImpl(String namespaceURI, String localName, String namespacePrefix, QName typeName) {
        super(namespaceURI, localName, namespacePrefix);
        super.setSchemaType(typeName);
    }
}

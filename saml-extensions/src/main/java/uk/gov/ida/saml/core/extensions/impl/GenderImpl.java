package uk.gov.ida.saml.core.extensions.impl;

import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.Unmarshaller;
import uk.gov.ida.saml.core.extensions.Gender;

import javax.xml.namespace.QName;

public class GenderImpl extends StringBasedMdsAttributeValueImpl implements Gender {
    public static final Marshaller MARSHALLER = new StringBasedMdsAttributeValueMarshaller(Gender.TYPE_LOCAL_NAME);
    public static final Unmarshaller UNMARSHALLER = new StringBasedMdsAttributeValueUnmarshaller();

    protected GenderImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        this(namespaceURI, elementLocalName, namespacePrefix, Gender.TYPE_NAME);
    }

    protected GenderImpl(String namespaceURI, String elementLocalName, String namespacePrefix, QName typeName) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        super.setSchemaType(typeName);
    }
}

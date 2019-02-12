package uk.gov.ida.saml.core.extensions.impl;


import org.opensaml.core.xml.io.Marshaller;
import uk.gov.ida.saml.core.extensions.Gpg45Status;

public class Gpg45StatusImpl extends StringBasedMdsAttributeValueImpl implements Gpg45Status {
    public static final Marshaller MARSHALLER = new StringBasedMdsAttributeValueMarshaller(Gpg45Status.TYPE_LOCAL_NAME);

    protected Gpg45StatusImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
}

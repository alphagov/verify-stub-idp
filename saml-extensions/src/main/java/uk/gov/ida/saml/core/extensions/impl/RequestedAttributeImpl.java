package uk.gov.ida.saml.core.extensions.impl;

import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.saml.saml2.core.impl.AttributeImpl;
import uk.gov.ida.saml.core.extensions.RequestedAttribute;

public class RequestedAttributeImpl extends AttributeImpl implements RequestedAttribute {
    private Boolean isRequired;

    public static final Marshaller MARSHALLER = new RequestedAttributeMarshaller();
    public static final Unmarshaller UNMARSHALLER = new RequestedAttributeUnmarshaller();

    protected RequestedAttributeImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    @Override
    public Boolean isRequired() {
        return isRequired;
    }

    @Override
    public void setIsRequired(Boolean isRequired) {
        this.isRequired = prepareForAssignment(this.isRequired, isRequired);
    }
}


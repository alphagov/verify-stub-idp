package uk.gov.ida.saml.core.extensions.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.impl.AttributeMarshaller;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.extensions.RequestedAttribute;

class RequestedAttributeMarshaller extends AttributeMarshaller {

    protected void marshallAttributes(XMLObject samlObject, Element domElement) throws MarshallingException {
        RequestedAttribute requestedAttribute = (RequestedAttribute) samlObject;

        if (requestedAttribute.isRequired() != null) {
            domElement.setAttributeNS(null, RequestedAttribute.IS_REQUIRED_ATTRIB_NAME, requestedAttribute.isRequired().toString());
        }

        super.marshallAttributes(samlObject, domElement);
    }
}

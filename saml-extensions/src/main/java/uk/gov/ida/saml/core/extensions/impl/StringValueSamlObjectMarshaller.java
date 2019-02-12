package uk.gov.ida.saml.core.extensions.impl;


import net.shibboleth.utilities.java.support.xml.ElementSupport;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.extensions.StringValueSamlObject;

public class StringValueSamlObjectMarshaller extends AbstractSAMLObjectMarshaller {

    @Override
    protected void marshallElementContent(XMLObject xmlObject, Element domElement) throws MarshallingException {
        StringValueSamlObject stringValueSamlObject = (StringValueSamlObject) xmlObject;
        ElementSupport.appendTextContent(domElement, stringValueSamlObject.getValue());
    }
}

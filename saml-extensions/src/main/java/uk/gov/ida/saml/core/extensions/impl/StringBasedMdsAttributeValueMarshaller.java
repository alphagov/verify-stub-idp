package uk.gov.ida.saml.core.extensions.impl;

import net.shibboleth.utilities.java.support.xml.ElementSupport;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.extensions.StringBasedMdsAttributeValue;

public class StringBasedMdsAttributeValueMarshaller extends BaseMdsSamlObjectMarshaller {

    public StringBasedMdsAttributeValueMarshaller(){
        super();
    }

    public StringBasedMdsAttributeValueMarshaller(String xsiType){
        super(xsiType);
    }

    @Override
    protected void marshallElementContent(XMLObject xmlObject, Element domElement) throws MarshallingException {
        StringBasedMdsAttributeValue stringBasedMdsAttributeValue = (StringBasedMdsAttributeValue) xmlObject;
        ElementSupport.appendTextContent(domElement, stringBasedMdsAttributeValue.getValue());
    }
}

package uk.gov.ida.saml.core.extensions.impl;


import net.shibboleth.utilities.java.support.xml.ElementSupport;
import net.shibboleth.utilities.java.support.xml.NamespaceSupport;
import net.shibboleth.utilities.java.support.xml.XMLConstants;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.extensions.BooleanBasedMdsAttributeValue;

public class BooleanBasedMdsAttributeValueMarshaller extends AbstractSAMLObjectMarshaller {

    private final String xsiType;

    public BooleanBasedMdsAttributeValueMarshaller(String xsiType){
        this.xsiType = xsiType;
    }

    @Override
    protected void marshallElementContent(XMLObject xmlObject, Element domElement) throws MarshallingException {
        BooleanBasedMdsAttributeValue booleanBasedMdsAttributeValue = (BooleanBasedMdsAttributeValue) xmlObject;
        ElementSupport.appendTextContent(domElement, String.valueOf(booleanBasedMdsAttributeValue.getValue()));
    }

    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        if(xsiType != null){
            NamespaceSupport.appendNamespaceDeclaration(domElement, XMLConstants.XSI_NS, XMLConstants.XSI_PREFIX);
            XMLObjectSupport.marshallAttribute(XMLConstants.XSI_TYPE_ATTRIB_NAME, IdaConstants.IDA_PREFIX + ":" + xsiType, domElement, false);
            NamespaceSupport.appendNamespaceDeclaration(domElement, IdaConstants.IDA_NS, IdaConstants.IDA_PREFIX);
        }

        super.marshallAttributes(xmlObject, domElement);
    }
}

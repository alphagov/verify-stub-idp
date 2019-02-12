package uk.gov.ida.saml.core.extensions.impl;


import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import uk.gov.ida.saml.core.extensions.StringValueSamlObject;

public class StringValueSamlObjectUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    @Override
    protected void processElementContent(XMLObject samlObject, String elementContent) {
        StringValueSamlObject stringValueSamlObject = (StringValueSamlObject) samlObject;
        stringValueSamlObject.setValue(elementContent);
    }
}

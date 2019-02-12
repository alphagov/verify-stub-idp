package uk.gov.ida.saml.core.extensions.impl;

import org.opensaml.core.xml.XMLObject;
import uk.gov.ida.saml.core.extensions.StringBasedMdsAttributeValue;

public class StringBasedMdsAttributeValueUnmarshaller extends BaseMdsSamlObjectUnmarshaller {

    @Override
    protected void processElementContent(XMLObject samlObject, String elementContent) {
        StringBasedMdsAttributeValue stringBasedMdsAttributeValue = (StringBasedMdsAttributeValue) samlObject;
        stringBasedMdsAttributeValue.setValue(elementContent);
    }
}

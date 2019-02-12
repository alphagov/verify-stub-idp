package uk.gov.ida.saml.core.extensions.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;
import uk.gov.ida.saml.core.extensions.LocalisableAttributeValue;

public class LocalisableStringBasedMdsAttributeValueUnmarshaller extends StringBasedMdsAttributeValueUnmarshaller {

    @Override
    protected void processAttribute(XMLObject samlObject, Attr attribute) throws UnmarshallingException {
        LocalisableAttributeValue localisableAttributeValue = (LocalisableAttributeValue) samlObject;
        if (attribute.getLocalName().equals(LocalisableAttributeValue.LANGUAGE_ATTRIB_NAME)) {
            localisableAttributeValue.setLanguage(attribute.getValue());
        } else {
            super.processAttribute(samlObject, attribute);
        }
    }
}

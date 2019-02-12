package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.core.xml.XMLObject;
import uk.gov.ida.saml.core.extensions.eidas.CurrentFamilyName;

public class CurrentFamilyNameUnmarshaller extends AbstractTransliterableStringUnmarshaller {

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject samlObject, String elementContent) {
        CurrentFamilyName currentFamilyName = (CurrentFamilyName) samlObject;
        currentFamilyName.setFamilyName(elementContent);
    }
}

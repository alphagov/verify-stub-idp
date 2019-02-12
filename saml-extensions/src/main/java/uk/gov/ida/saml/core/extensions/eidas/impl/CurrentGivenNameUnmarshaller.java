package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.core.xml.XMLObject;
import uk.gov.ida.saml.core.extensions.eidas.CurrentGivenName;

public class CurrentGivenNameUnmarshaller extends AbstractTransliterableStringUnmarshaller {

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject samlObject, String elementContent) {
        CurrentGivenName currentGivenName = (CurrentGivenName) samlObject;
        currentGivenName.setFirstName(elementContent);
    }
}

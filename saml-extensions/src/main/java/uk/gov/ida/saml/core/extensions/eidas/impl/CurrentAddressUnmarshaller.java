package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import uk.gov.ida.saml.core.extensions.eidas.CurrentAddress;

public class CurrentAddressUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject samlObject, String elementContent) {
        CurrentAddress currentAddress = (CurrentAddress) samlObject;
        currentAddress.setCurrentAddress(elementContent);
    }
}

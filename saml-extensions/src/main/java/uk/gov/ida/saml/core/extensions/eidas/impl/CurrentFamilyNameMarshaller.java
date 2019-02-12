package uk.gov.ida.saml.core.extensions.eidas.impl;

import net.shibboleth.utilities.java.support.xml.ElementSupport;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.extensions.eidas.CurrentFamilyName;

public class CurrentFamilyNameMarshaller extends AbstractTransliterableStringMarshaller {

    /** {@inheritDoc} */
    protected void marshallElementContent(XMLObject samlObject, Element domElement) throws MarshallingException {
        CurrentFamilyName currentFamilyName = (CurrentFamilyName) samlObject;
        ElementSupport.appendTextContent(domElement, currentFamilyName.getFamilyName());
    }
}

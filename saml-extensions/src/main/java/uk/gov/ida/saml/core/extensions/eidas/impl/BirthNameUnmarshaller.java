package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import uk.gov.ida.saml.core.extensions.eidas.BirthName;

public class BirthNameUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject samlObject, String elementContent) {
        BirthName birthName = (BirthName) samlObject;
        birthName.setBirthName(elementContent);
    }
}

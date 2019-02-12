package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import uk.gov.ida.saml.core.extensions.eidas.Gender;

public class GenderUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject samlObject, String elementContent) {
        Gender gender = (Gender) samlObject;
        gender.setValue(elementContent);
    }
}

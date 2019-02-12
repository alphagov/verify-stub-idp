package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import uk.gov.ida.saml.core.extensions.eidas.PlaceOfBirth;

public class PlaceOfBirthUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject samlObject, String elementContent) {
        PlaceOfBirth placeOfBirth = (PlaceOfBirth) samlObject;
        placeOfBirth.setPlaceOfBirth(elementContent);
    }
}

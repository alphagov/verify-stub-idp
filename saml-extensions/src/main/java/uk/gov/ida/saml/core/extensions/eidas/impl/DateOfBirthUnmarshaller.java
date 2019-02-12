package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import uk.gov.ida.saml.core.extensions.eidas.DateOfBirth;

import static uk.gov.ida.saml.core.extensions.eidas.impl.DateOfBirthImpl.DATE_OF_BIRTH_FORMAT;

public class DateOfBirthUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject samlObject, String elementContent) {
        DateOfBirth dateOfBirth = (DateOfBirth) samlObject;
        dateOfBirth.setDateOfBirth(DATE_OF_BIRTH_FORMAT.parseLocalDate(elementContent));
    }
}

package uk.gov.ida.saml.core.extensions.eidas.impl;

import net.shibboleth.utilities.java.support.xml.ElementSupport;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.extensions.eidas.DateOfBirth;

import static uk.gov.ida.saml.core.extensions.eidas.impl.DateOfBirthImpl.DATE_OF_BIRTH_FORMAT;

public class DateOfBirthMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    protected void marshallElementContent(XMLObject samlObject, Element domElement) throws MarshallingException {
        DateOfBirth dateOfBirth = (DateOfBirth) samlObject;
        ElementSupport.appendTextContent(domElement, DATE_OF_BIRTH_FORMAT.print(dateOfBirth.getDateOfBirth()));
    }
}

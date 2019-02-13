package uk.gov.ida.saml.core.extensions.eidas.impl;

import net.shibboleth.utilities.java.support.xml.ElementSupport;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.extensions.eidas.BirthName;

public class BirthNameMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    protected void marshallElementContent(XMLObject samlObject, Element domElement) throws MarshallingException {
        BirthName birthName = (BirthName) samlObject;
        ElementSupport.appendTextContent(domElement, birthName.getBirthName());
    }
}

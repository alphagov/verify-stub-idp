package uk.gov.ida.saml.core.extensions.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import uk.gov.ida.saml.core.extensions.Address;
import uk.gov.ida.saml.core.extensions.InternationalPostCode;
import uk.gov.ida.saml.core.extensions.Line;
import uk.gov.ida.saml.core.extensions.PostCode;
import uk.gov.ida.saml.core.extensions.UPRN;

public class AddressUnmarshaller extends BaseMdsSamlObjectUnmarshaller {

    protected void processChildElement(XMLObject parentObject, XMLObject childObject) throws UnmarshallingException {
        Address address = (Address) parentObject;

        if (childObject instanceof Line) {
            address.getLines().add((Line) childObject);
        } else if (childObject instanceof PostCode) {
            address.setPostCode((PostCode) childObject);
        } else if (childObject instanceof InternationalPostCode) {
            address.setInternationalPostCode((InternationalPostCode) childObject);
        } else if (childObject instanceof UPRN) {
            address.setUPRN((UPRN) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }
}

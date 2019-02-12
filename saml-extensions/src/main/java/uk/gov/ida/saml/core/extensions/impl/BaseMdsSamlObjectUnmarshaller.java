package uk.gov.ida.saml.core.extensions.impl;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.w3c.dom.Attr;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.extensions.Address;
import uk.gov.ida.saml.core.extensions.BaseMdsSamlObject;

public class BaseMdsSamlObjectUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    protected void processAttribute(XMLObject samlObject, Attr attribute) throws UnmarshallingException {
        BaseMdsSamlObject address = (BaseMdsSamlObject) samlObject;

        switch (attribute.getLocalName()) {
            case Address.FROM_ATTRIB_NAME:
                address.setFrom(DateTime.parse(attribute.getValue(), DateTimeFormat.forPattern(IdaConstants.DATETIME_FORMAT).withZone(DateTimeZone.UTC)));
                break;
            case Address.TO_ATTRIB_NAME:
                address.setTo(DateTime.parse(attribute.getValue(), DateTimeFormat.forPattern(IdaConstants.DATETIME_FORMAT).withZone(DateTimeZone.UTC)));
                break;
            case Address.VERIFIED_ATTRIB_NAME:
                address.setVerified(Boolean.parseBoolean(attribute.getValue()));
                break;
            default:
                super.processAttribute(samlObject, attribute);
        }
    }
}

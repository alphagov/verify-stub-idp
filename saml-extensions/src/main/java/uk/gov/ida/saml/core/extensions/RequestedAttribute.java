package uk.gov.ida.saml.core.extensions;

import org.opensaml.saml.saml2.core.Attribute;
import uk.gov.ida.saml.core.IdaConstants;

import javax.xml.namespace.QName;

public interface RequestedAttribute extends Attribute {

    /** Element local name. */
    String DEFAULT_ELEMENT_LOCAL_NAME = "RequestedAttribute";

    /** Default element name. */
    QName DEFAULT_ELEMENT_NAME = new QName(IdaConstants.EIDAS_NS, DEFAULT_ELEMENT_LOCAL_NAME, IdaConstants.EIDAS_PREFIX);

    /**
     * Local name of the XSI type.
     */
    String TYPE_LOCAL_NAME = "RequestedAttributeType";

    /**
     * QName of the XSI type.
     */
    QName TYPE_NAME = new QName(IdaConstants.EIDAS_NS, TYPE_LOCAL_NAME, IdaConstants.EIDAS_PREFIX);

    String IS_REQUIRED_ATTRIB_NAME = "isRequired";

    Boolean isRequired();

    void setIsRequired(Boolean isRequired);
}

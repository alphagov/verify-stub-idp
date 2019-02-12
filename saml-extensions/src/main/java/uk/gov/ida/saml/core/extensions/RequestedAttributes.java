package uk.gov.ida.saml.core.extensions;

import org.opensaml.saml.common.SAMLObject;
import uk.gov.ida.saml.core.IdaConstants;

import javax.xml.namespace.QName;

public interface RequestedAttributes extends SAMLObject {

    /** Element local name. */
    String DEFAULT_ELEMENT_LOCAL_NAME = "RequestedAttributes";

    /** Default element name. */
    QName DEFAULT_ELEMENT_NAME = new QName(IdaConstants.EIDAS_NS, DEFAULT_ELEMENT_LOCAL_NAME, IdaConstants.EIDAS_PREFIX);

    /**
     * Local name of the XSI type.
     */
    String TYPE_LOCAL_NAME = "RequestedAttributesType";

    /**
     * QName of the XSI type.
     */
    QName TYPE_NAME = new QName(IdaConstants.EIDAS_NS, TYPE_LOCAL_NAME, IdaConstants.EIDAS_PREFIX);

    void addRequestedAttribute(RequestedAttribute requestedAttribute);
}

package uk.gov.ida.saml.core.extensions.eidas;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AttributeValue;
import uk.gov.ida.saml.core.IdaConstants;

import javax.xml.namespace.QName;

import static uk.gov.ida.saml.core.IdaConstants.EIDAS_NATURAL_PERSON_NS;

public interface Gender extends AttributeValue {

    /** Element local name. */
    String DEFAULT_ELEMENT_LOCAL_NAME = "AttributeValue";

    /** Default element name. */
    QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);

    /**  Local name of the XSI type. */
    String TYPE_LOCAL_NAME = "GenderType";

    /** QName of the XSI type. */
    QName TYPE_NAME = new QName(EIDAS_NATURAL_PERSON_NS, TYPE_LOCAL_NAME, IdaConstants.EIDAS_NATURUAL_PREFIX);

    /**
     * Return the gender value.
     *
     * @return the gender value
     */
    public String getValue();

    /**
     * Set the gender value.
     *
     * @param gender the gender value
     */
    public void setValue(String gender);
}

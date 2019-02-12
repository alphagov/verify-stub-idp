package uk.gov.ida.saml.core.extensions.eidas;

import org.opensaml.saml.common.xml.SAMLConstants;
import uk.gov.ida.saml.core.IdaConstants;

import javax.xml.namespace.QName;

import static uk.gov.ida.saml.core.IdaConstants.EIDAS_NATURAL_PERSON_NS;

public interface CurrentGivenName extends TransliterableString {

    /** Element local name. */
    String DEFAULT_ELEMENT_LOCAL_NAME = "AttributeValue";

    /** Default element name. */
    QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);

    /**  Local name of the XSI type. */
    String TYPE_LOCAL_NAME = "CurrentGivenNameType";

    /** QName of the XSI type. */
    QName TYPE_NAME = new QName(EIDAS_NATURAL_PERSON_NS, TYPE_LOCAL_NAME, IdaConstants.EIDAS_NATURUAL_PREFIX);

    /**
     * Return the given name.
     *
     * @return the given name
     */
    public String getFirstName();

    /**
     * Set the given name.
     *
     * @param firstName the given name
     */
    public void setFirstName(String firstName);
}

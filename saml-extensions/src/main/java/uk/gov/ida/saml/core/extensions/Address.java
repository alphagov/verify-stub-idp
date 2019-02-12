package uk.gov.ida.saml.core.extensions;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AttributeValue;
import uk.gov.ida.saml.core.IdaConstants;

import javax.xml.namespace.QName;
import java.util.List;

public interface Address extends AttributeValue, BaseMdsSamlObject {

    /** Element local name. */
    String DEFAULT_ELEMENT_LOCAL_NAME = "AttributeValue";

    /** Default element name. */
    QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);

    /**
     * Local name of the XSI type.
     */
    String TYPE_LOCAL_NAME = "AddressType";

    /**
     * QName of the XSI type.
     */
    QName TYPE_NAME = new QName(IdaConstants.IDA_NS, TYPE_LOCAL_NAME, IdaConstants.IDA_PREFIX);

    List<Line> getLines();

    PostCode getPostCode();

    void setPostCode(PostCode postCode);

    InternationalPostCode getInternationalPostCode();

    void setInternationalPostCode(InternationalPostCode internationalPostCode);

    UPRN getUPRN();

    void setUPRN(UPRN uprn);
}

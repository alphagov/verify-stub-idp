package uk.gov.ida.saml.core.extensions;

import javax.xml.namespace.QName;

public interface StatusValue extends StringValueSamlObject {

    public static String CANCEL = "authn-cancel";
    public static String PENDING = "loa-pending";
    public static String UPLIFT_FAILED = "uplift-failed";

    /** Element local name. */
    String DEFAULT_ELEMENT_LOCAL_NAME = "StatusValue";

    /** Default element name. */
    QName DEFAULT_ELEMENT_NAME = new QName(DEFAULT_ELEMENT_LOCAL_NAME);

    /**
     * Local name of the XSI type.
     */
    String TYPE_LOCAL_NAME = "StatusValue";

    /**
     * QName of the XSI type.
     */
    QName TYPE_NAME = new QName(TYPE_LOCAL_NAME);
}

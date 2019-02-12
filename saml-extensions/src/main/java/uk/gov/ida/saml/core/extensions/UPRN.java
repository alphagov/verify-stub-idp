package uk.gov.ida.saml.core.extensions;

import uk.gov.ida.saml.core.IdaConstants;

import javax.xml.namespace.QName;

public interface UPRN extends StringValueSamlObject {

    /** Element local name. */
    String DEFAULT_ELEMENT_LOCAL_NAME = "UPRN";

    /** Default element name. */
    QName DEFAULT_ELEMENT_NAME = new QName(IdaConstants.IDA_NS, DEFAULT_ELEMENT_LOCAL_NAME, IdaConstants.IDA_PREFIX);

    /**
     * Local name of the XSI type.
     */
    String TYPE_LOCAL_NAME = "UPRNType";

    /**
     * QName of the XSI type.
     */
    QName TYPE_NAME = new QName(IdaConstants.IDA_NS, TYPE_LOCAL_NAME, IdaConstants.IDA_PREFIX);
}

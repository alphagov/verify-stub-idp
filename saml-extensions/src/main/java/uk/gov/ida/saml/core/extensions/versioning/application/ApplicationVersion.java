package uk.gov.ida.saml.core.extensions.versioning.application;

import uk.gov.ida.saml.core.extensions.StringValueSamlObject;
import uk.gov.ida.saml.core.extensions.versioning.Version;

import javax.xml.namespace.QName;

public interface ApplicationVersion extends StringValueSamlObject {
    String DEFAULT_ELEMENT_LOCAL_NAME = "ApplicationVersion";

    QName DEFAULT_ELEMENT_NAME = new QName(Version.NAMESPACE_URI, DEFAULT_ELEMENT_LOCAL_NAME, Version.NAMESPACE_PREFIX);
}

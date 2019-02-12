package uk.gov.ida.saml.core.extensions.versioning.application;

import uk.gov.ida.saml.core.extensions.impl.StringValueSamlObjectImpl;
import uk.gov.ida.saml.core.extensions.versioning.Version;

public class ApplicationVersionImpl extends StringValueSamlObjectImpl implements ApplicationVersion {

    public ApplicationVersionImpl() {
        super(Version.NAMESPACE_URI, ApplicationVersion.DEFAULT_ELEMENT_LOCAL_NAME, Version.NAMESPACE_PREFIX);
    }

    public ApplicationVersionImpl(String namespaceURI, String localName, String namespacePrefix) {
        super(namespaceURI, localName, namespacePrefix);
    }
}

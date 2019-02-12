package uk.gov.ida.saml.core.extensions.versioning;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import uk.gov.ida.saml.core.extensions.versioning.application.ApplicationVersion;

public class VersionUnMarshaller extends AbstractSAMLObjectUnmarshaller {

    protected void processChildElement(XMLObject parentObject, XMLObject childObject) throws UnmarshallingException {
        Version version = (Version) parentObject;
        version.setApplicationVersion((ApplicationVersion) childObject);
    }
}

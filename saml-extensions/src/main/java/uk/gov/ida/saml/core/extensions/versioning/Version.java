package uk.gov.ida.saml.core.extensions.versioning;

import org.opensaml.saml.saml2.core.AttributeValue;
import uk.gov.ida.saml.core.extensions.versioning.application.ApplicationVersion;

import javax.xml.namespace.QName;

public interface Version extends AttributeValue {

    String TYPE_LOCAL_NAME = "VersionType";
    String NAMESPACE_URI = "http://www.cabinetoffice.gov.uk/resource-library/ida/metrics";
    String NAMESPACE_PREFIX = "metric";

    QName TYPE_NAME = new QName(NAMESPACE_URI, TYPE_LOCAL_NAME, NAMESPACE_PREFIX);

    ApplicationVersion getApplicationVersion();

    void setApplicationVersion(ApplicationVersion applicationVersion);
}

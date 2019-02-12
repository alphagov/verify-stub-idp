package uk.gov.ida.saml.core.extensions.versioning;

import net.shibboleth.utilities.java.support.xml.XMLConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.extensions.versioning.application.ApplicationVersionImpl;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(OpenSAMLRunner.class)
public class VersionMarshallerTest {

    private VersionMarshaller versionMarshaller = new VersionMarshaller();

    @Test
    public void shouldMarshallVersion() throws Exception {
        Version version = new VersionBuilder().buildObject();
        ApplicationVersionImpl applicationVersion = new ApplicationVersionImpl();
        applicationVersion.setValue("some-version-value");
        version.setApplicationVersion(applicationVersion);

        Element marshaledVersion = versionMarshaller.marshall(version);

        assertThat(marshaledVersion.getAttribute(XMLConstants.XMLNS_PREFIX + ":" + Version.NAMESPACE_PREFIX)).isEqualTo("http://www.cabinetoffice.gov.uk/resource-library/ida/metrics");
        assertThat(marshaledVersion.getAttributeNS(XMLConstants.XSI_NS, XMLConstants.XSI_TYPE_ATTRIB_NAME.getLocalPart())).isEqualTo("metric:VersionType");
        assertThat(marshaledVersion.getFirstChild().getNodeName()).isEqualTo("metric:ApplicationVersion");
        assertThat(marshaledVersion.getFirstChild().getTextContent()).isEqualTo("some-version-value");
    }
}
package uk.gov.ida.metadata.transformers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import uk.gov.ida.common.shared.security.Certificate;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;
import uk.gov.ida.saml.metadata.transformers.KeyDescriptorsUnmarshaller;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.core.test.builders.CertificateBuilder.aCertificate;

@RunWith(OpenSAMLRunner.class)
public class KeyDescriptorsUnmarshallerTest {

    private final KeyDescriptorsUnmarshaller factory = new KeyDescriptorsUnmarshaller(new OpenSamlXmlObjectFactory());

    @Test
    public void transform_shouldTransformCertificate() throws Exception {
        Certificate certificate = aCertificate().build();
        List<KeyDescriptor> keyDescriptors = factory.fromCertificates(newArrayList(certificate));
        KeyDescriptor keyDescriptor = keyDescriptors.get(0);
        assertThat(keyDescriptor.getKeyInfo().getX509Datas().get(0).getX509Certificates().get(0).getValue()).isEqualTo(certificate.getCertificate());
    }

    @Test
    public void transform_shouldReturnExpectedNumberOfKeyDescriptors(){
        Certificate certificate = aCertificate().build();
        List<KeyDescriptor> keyDescriptors = factory.fromCertificates(newArrayList(certificate, certificate));
        assertThat(keyDescriptors.size()).isEqualTo(2);
    }
}

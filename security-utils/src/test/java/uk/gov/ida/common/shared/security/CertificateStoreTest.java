package uk.gov.ida.common.shared.security;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.shared.configuration.PublicKeyFileConfiguration;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.common.shared.security.Certificate.BEGIN_CERT;
import static uk.gov.ida.common.shared.security.Certificate.END_CERT;

@RunWith(MockitoJUnitRunner.class)
public class CertificateStoreTest {

    PublicKeyFileConfiguration publicKeyConfiguration;
    PublicKeyFileConfiguration publicKeyConfiguration2;

    @Before
    public void setup() throws Exception {
        publicKeyConfiguration = getPublicKey("public_key.crt");
        publicKeyConfiguration2 = getPublicKey("public_key_2.crt");
    }

    private PublicKeyFileConfiguration getPublicKey(String publicKey) throws IOException, URISyntaxException {
        return new PublicKeyFileConfiguration(getClass().getClassLoader().getResource(publicKey).getFile(), "TestCertificateName");
    }

    @Test
    public void getEncryptionCertificateValue_shouldStripOutHeadersIfPresent() throws UnsupportedEncodingException {
        CertificateStore certificateStore = new CertificateStore(ImmutableList.of(publicKeyConfiguration), ImmutableList.of(publicKeyConfiguration));
        String encryptionCertificateValue = certificateStore.getEncryptionCertificates().get(0).getCertificate();

        assertThat(encryptionCertificateValue.contains("BEGIN")).isEqualTo(false);
        assertThat(encryptionCertificateValue.contains("END")).isEqualTo(false);
        assertThat(publicKeyConfiguration.getCert()).contains(encryptionCertificateValue);
    }

    @Test
    public void getEncryptionCertificateValue_shouldHandleMultipleCertificateValues() throws UnsupportedEncodingException {
        CertificateStore certificateStore = new CertificateStore(ImmutableList.of(publicKeyConfiguration, publicKeyConfiguration2), ImmutableList.of(publicKeyConfiguration));
        final List<Certificate> encryptionCertificates = certificateStore.getEncryptionCertificates();

        encryptionCertificates.forEach(cert -> {
            assertThat(newArrayList(
                    stripHeaders(publicKeyConfiguration.getCert()),
                    stripHeaders(publicKeyConfiguration2.getCert()))).contains(cert.getCertificate());
        });
    }

    private String stripHeaders(final String originalCertificate) {
        return originalCertificate.replace(BEGIN_CERT, "").replace(END_CERT, "").replace(" ","");
    }

    @Test
    public void getSigningCertificateValue_shouldStripOutHeadersIfPresent() throws UnsupportedEncodingException {
        CertificateStore certificateStore = new CertificateStore(ImmutableList.of(publicKeyConfiguration), ImmutableList.of(publicKeyConfiguration2));
        List<Certificate> signingCertificateValues = certificateStore.getSigningCertificates();

        assertThat(signingCertificateValues).hasSize(1);

        Certificate primaryCertificate = signingCertificateValues.get(0);
        assertThat(primaryCertificate.getIssuerId()).isEqualTo("TestCertificateName");
        assertThat(publicKeyConfiguration2.getCert()).contains(primaryCertificate.getCertificate());
    }
}

package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.common.shared.security.Certificate.BEGIN_CERT;
import static uk.gov.ida.common.shared.security.Certificate.END_CERT;

public class DeserializablePublicKeyConfigurationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldDefaultToFileType() throws Exception {
        DeserializablePublicKeyConfiguration publicKeyConfiguration = objectMapper.readValue("{\"cert\": \"" + getCertPath() + "\", \"name\": \"someId\"}", DeserializablePublicKeyConfiguration.class);
        assertThat(publicKeyConfiguration.getClass()).isEqualTo(PublicKeyFileConfiguration.class);
    }

    @Test
    public void shouldUseFileTypeWhenSpecified() throws Exception {
        DeserializablePublicKeyConfiguration publicKeyConfiguration = objectMapper.readValue("{\"type\": \"file\", \"cert\": \"" + getCertPath() + "\", \"name\": \"someId\"}", DeserializablePublicKeyConfiguration.class);
        assertThat(publicKeyConfiguration.getClass()).isEqualTo(PublicKeyFileConfiguration.class);
    }

    @Test
    public void shouldUseEncodedTypeWhenSpecified() throws Exception {
        DeserializablePublicKeyConfiguration publicKeyConfiguration = objectMapper.readValue("{\"type\": \"encoded\", \"cert\": \"" + getBase64Cert() + "\", \"name\": \"someId\"}", DeserializablePublicKeyConfiguration.class);
        assertThat(publicKeyConfiguration.getClass()).isEqualTo(EncodedCertificateConfiguration.class);
    }

    @Test
    public void shouldUseX509TypeWhenSpecified() throws Exception {
        DeserializablePublicKeyConfiguration publicKeyConfiguration = objectMapper.readValue("{\"type\": \"x509\", \"cert\": \"" + getStrippedCert() + "\", \"name\": \"someId\"}", DeserializablePublicKeyConfiguration.class);
        assertThat(publicKeyConfiguration.getClass()).isEqualTo(X509CertificateConfiguration.class);
    }

    private String getCertificateString() {
        try {
            return new String(Files.readAllBytes(getCertPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getCertPath() {
        String path = Resources.getResource("public_key.crt").getFile();
        return new File(path).toPath();
    }

    private String getStrippedCert() {
        return getCertificateString()
                .replace(BEGIN_CERT, "")
                .replace(END_CERT, "")
                .replace("\n", "")
                .trim();
    }

    private String getBase64Cert() {
        return Base64.getEncoder().encodeToString(getCertificateString().getBytes());
    }

}
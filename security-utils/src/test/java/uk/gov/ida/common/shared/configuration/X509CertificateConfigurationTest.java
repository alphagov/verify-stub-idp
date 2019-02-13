package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.google.common.io.Resources;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.common.shared.security.Certificate.BEGIN_CERT;
import static uk.gov.ida.common.shared.security.Certificate.END_CERT;

public class X509CertificateConfigurationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void should_loadPublicKey() throws Exception {
        String strippedCert = getCertificateString();
        String jsonConfig = "{\"type\": \"x509\", \"cert\": \"" + strippedCert + "\", \"name\": \"someId\"}";
        DeserializablePublicKeyConfiguration config = objectMapper.readValue(jsonConfig, DeserializablePublicKeyConfiguration.class);

        assertThat(config.getPublicKey().getAlgorithm()).isEqualTo("RSA");
    }

    @Test
    public void should_loadPublicKeyWhenUsingAliases() throws Exception {
        String strippedCert = getCertificateString();
        List<String> aliases = Arrays.asList("cert", "x509");

        for (String alias : aliases) {
            DeserializablePublicKeyConfiguration publicKeyConfiguration = objectMapper.readValue(
                    "{\"type\": \"x509\", \"" + alias + "\": \"" + strippedCert + "\", \"name\": \"someId\"}",
                    DeserializablePublicKeyConfiguration.class);

            assertThat(publicKeyConfiguration.getPublicKey().getAlgorithm()).isEqualTo("RSA");
        }
    }

    @Test
    public void should_ThrowExceptionWhenStringDoesNotContainAPublicKey() throws Exception {
        thrown.expect(InvalidDefinitionException.class);
        thrown.expectMessage("Unable to load certificate");
        String encodedKey = Base64.getEncoder().encodeToString("not-a-fullCertificate".getBytes());
        objectMapper.readValue("{\"type\": \"x509\", \"cert\": \"" + encodedKey + "\", \"name\": \"someId\"}", DeserializablePublicKeyConfiguration.class);
    }

    @Test(expected = InvalidDefinitionException.class)
    public void should_ThrowExceptionWhenIncorrectKeySpecified() throws Exception {
        String path = getClass().getClassLoader().getResource("empty_file").getPath();
        String jsonConfig = "{\"type\": \"x509\", \"certFoo\": \"" + path + "\", \"name\": \"someId\"}";
        objectMapper.readValue(jsonConfig, DeserializablePublicKeyConfiguration.class);
    }

    private String getCertificateString() throws IOException {
        String path = Resources.getResource("public_key.crt").getFile();
        byte[] cert = Files.readAllBytes(new File(path).toPath());
        String fullCert = new String(cert);
        return fullCert
                .replace(BEGIN_CERT, "")
                .replace(END_CERT, "")
                .replace("\n", "")
                .trim();
    }

}
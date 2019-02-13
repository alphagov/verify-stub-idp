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

public class EncodedCertificateConfigurationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void should_loadPublicKeyFromJSON() throws Exception {
        String encodedCert = getCertificateAsString();
        String jsonConfig = "{\"type\": \"encoded\", \"cert\": \"" + encodedCert + "\", \"name\": \"someId\"}";
        DeserializablePublicKeyConfiguration config = objectMapper.readValue(jsonConfig, DeserializablePublicKeyConfiguration.class);

        assertThat(config.getPublicKey().getAlgorithm()).isEqualTo("RSA");
    }

    @Test
    public void should_ThrowExceptionWhenStringDoesNotContainAPublicKey() throws Exception {
        thrown.expect(InvalidDefinitionException.class);
        thrown.expectMessage("Unable to load certificate");
        String path = Resources.getResource("private_key.pk8").getFile();
        byte[] key = Files.readAllBytes(new File(path).toPath());
        String encodedKey = Base64.getEncoder().encodeToString(key);
        objectMapper.readValue("{\"type\": \"encoded\", \"cert\": \"" + encodedKey + "\", \"name\": \"someId\"}", DeserializablePublicKeyConfiguration.class);
    }

    @Test
    public void should_ThrowExceptionWhenStringIsNotBase64Encoded() throws Exception {
        thrown.expect(InvalidDefinitionException.class);

        objectMapper.readValue("{\"type\": \"encoded\", \"cert\": \"" + "FOOBARBAZ" + "\", \"name\": \"someId\"}", DeserializablePublicKeyConfiguration.class);
    }

    @Test(expected = InvalidDefinitionException.class)
    public void should_ThrowExceptionWhenIncorrectKeySpecified() throws Exception {
        String path = getClass().getClassLoader().getResource("empty_file").getPath();
        String jsonConfig = "{\"type\": \"encoded\", \"certFoo\": \"" + path + "\", \"name\": \"someId\"}";
        objectMapper.readValue(jsonConfig, DeserializablePublicKeyConfiguration.class);
    }

    private String getCertificateAsString() throws IOException {
        String path = Resources.getResource("public_key.crt").getFile();
        byte[] cert = Files.readAllBytes(new File(path).toPath());
        return Base64.getEncoder().encodeToString(cert);
    }

}
package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
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

public class EncodedPrivateKeyConfigurationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void should_loadPrivateKeyFromJSON() throws Exception {
        String jsonConfig = "{\"type\": \"encoded\", \"key\": \"" + getKeyAsBase64() + "\"}";
        PrivateKeyConfiguration configuration = objectMapper.readValue(jsonConfig, PrivateKeyConfiguration.class);
        assertThat(configuration.getPrivateKey().getAlgorithm()).isEqualTo("RSA");
    }

    @Test
    public void should_ThrowExceptionWhenKeyIsNotBase64() throws Exception {
        thrown.expect(InvalidDefinitionException.class);

        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false).readValue("{\"type\": \"encoded\", \"key\": \"not-a-key\"}", PrivateKeyConfiguration.class);
    }

    @Test
    public void should_ThrowExceptionWhenKeyIsNotAValidKey() throws Exception {
        thrown.expect(InvalidDefinitionException.class);
        thrown.expectMessage("InvalidKeySpecException");

        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false).readValue("{\"type\": \"encoded\", \"key\": \"dGVzdAo=\"}", PrivateKeyConfiguration.class);
    }

    @Test(expected = InvalidDefinitionException.class)
    public void should_throwAnExceptionWhenIncorrectFieldSpecified() throws Exception {
        objectMapper.readValue("{\"privateKeyFoo\": \"" + "foobar" + "\"}", PrivateKeyConfiguration.class);
    }

    private String getKeyAsBase64() throws IOException {
        String path = Resources.getResource("private_key.pk8").getFile();
        byte[] key = Files.readAllBytes(new File(path).toPath());
        return Base64.getEncoder().encodeToString(key);
    }
}
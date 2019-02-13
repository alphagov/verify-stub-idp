package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PublicKeyFileConfigurationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void should_loadPublicKeyFromJSON() throws Exception {
        String path = getClass().getClassLoader().getResource("public_key.crt").getPath();
        DeserializablePublicKeyConfiguration publicKeyConfiguration = objectMapper.readValue("{\"type\": \"file\", \"cert\": \"" + path + "\", \"name\": \"someId\"}", DeserializablePublicKeyConfiguration.class);

        assertThat(publicKeyConfiguration.getPublicKey().getAlgorithm()).isEqualTo("RSA");
    }

    @Test
    public void should_loadPublicKeyWhenUsingAliases() throws Exception {
        String path = getClass().getClassLoader().getResource("public_key.crt").getPath();
        List<String> aliases = Arrays.asList("cert", "certFile");

        for (String alias : aliases) {
            DeserializablePublicKeyConfiguration publicKeyConfiguration = objectMapper.readValue(
                    "{\"type\": \"file\", \"" + alias + "\": \"" + path + "\", \"name\": \"someId\"}",
                    DeserializablePublicKeyConfiguration.class);

            assertThat(publicKeyConfiguration.getPublicKey().getAlgorithm()).isEqualTo("RSA");
        }
    }

    @Test
    public void should_ThrowExceptionWhenFileDoesNotExist() throws Exception {
        thrown.expect(InvalidDefinitionException.class);
        thrown.expectMessage("NoSuchFileException");
        objectMapper.readValue("{\"type\": \"file\", \"cert\": \"/foo/bar\", \"name\": \"someId\"}", DeserializablePublicKeyConfiguration.class);
    }

    @Test
    public void should_ThrowExceptionWhenFileDoesNotContainAPublicKey() throws Exception {
        thrown.expect(InvalidDefinitionException.class);
        thrown.expectMessage("Unable to load certificate");

        String path = getClass().getClassLoader().getResource("empty_file").getPath();
        objectMapper.readValue("{\"type\": \"file\", \"cert\": \"" + path + "\", \"name\": \"someId\"}", DeserializablePublicKeyConfiguration.class);
    }

    @Test(expected = InvalidDefinitionException.class)
    public void should_ThrowExceptionWhenIncorrectKeySpecified() throws Exception {
        String path = getClass().getClassLoader().getResource("empty_file").getPath();
        objectMapper.readValue("{\"type\": \"file\", \"certFoo\": \"" + path + "\", \"name\": \"someId\"}", DeserializablePublicKeyConfiguration.class);
    }
}

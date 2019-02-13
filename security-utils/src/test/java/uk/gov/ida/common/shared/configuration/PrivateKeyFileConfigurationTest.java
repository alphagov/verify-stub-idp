package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PrivateKeyFileConfigurationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void should_loadPrivateKeyFromJSON() throws Exception {
        String path = getClass().getClassLoader().getResource("private_key.pk8").getPath();
        PrivateKeyConfiguration privateKeyFileConfiguration = objectMapper.readValue("{\"type\": \"file\", \"keyFile\": \"" + path + "\"}", PrivateKeyConfiguration.class);

        assertThat(privateKeyFileConfiguration.getPrivateKey().getAlgorithm()).isEqualTo("RSA");
    }

    @Test
    public void should_loadPrivateKeyWhenUsingAliases() throws Exception {
        String path = getClass().getClassLoader().getResource("private_key.pk8").getPath();
        List<String> aliases = Arrays.asList("key", "keyFile");

        for (String alias : aliases) {
            PrivateKeyConfiguration privateKeyFileConfiguration = objectMapper.readValue("{\"type\": \"file\", \"" + alias + "\": \"" + path + "\"}", PrivateKeyConfiguration.class);
            assertThat(privateKeyFileConfiguration.getPrivateKey().getAlgorithm()).isEqualTo("RSA");
        }
    }

    @Test
    public void should_ThrowExceptionWhenFileDoesNotExist() throws Exception {
        thrown.expect(InvalidDefinitionException.class);
        thrown.expectMessage("NoSuchFileException");

        objectMapper.readValue("{\"keyFile\": \"/foo/bar\"}", PrivateKeyConfiguration.class);
    }

    @Test
    public void should_ThrowExceptionWhenFileDoesNotContainAPrivateKey() throws Exception {
        thrown.expect(InvalidDefinitionException.class);
        thrown.expectMessage("InvalidKeySpecException");

        String path = getClass().getClassLoader().getResource("empty_file").getPath();
        objectMapper.readValue("{\"keyFile\": \"" + path + "\"}", PrivateKeyConfiguration.class);
    }

    @Test(expected = InvalidDefinitionException.class)
    public void should_throwAnExceptionWhenIncorrectJSONKeySpecified() throws Exception {
        String path = getClass().getClassLoader().getResource("empty_file").getPath();
        objectMapper.readValue("{\"privateKeyFoo\": \"" + path + "\"}", PrivateKeyConfiguration.class);
    }
}

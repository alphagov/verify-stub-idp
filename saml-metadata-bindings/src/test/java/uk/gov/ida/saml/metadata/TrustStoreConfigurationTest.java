package uk.gov.ida.saml.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import keystore.KeyStoreRule;
import keystore.builders.KeyStoreRuleBuilder;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.core.test.TestCertificateStrings.HUB_TEST_PUBLIC_SIGNING_CERT;

public class TrustStoreConfigurationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ObjectMapper objectMapper = new ObjectMapper();

    @ClassRule
    public static KeyStoreRule keyStoreRule = KeyStoreRuleBuilder.aKeyStoreRule().withCertificate("hub", HUB_TEST_PUBLIC_SIGNING_CERT).build();

    @Test
    public void shouldLoadTrustStoreFromFile() throws IOException {
        String jsonConfig = "{\"type\": \"file\", \"trustStorePath\": " + objectMapper.writeValueAsString(keyStoreRule.getAbsolutePath()) + ", \"trustStorePassword\": \"" + keyStoreRule.getPassword() + "\"}";
        TrustStoreConfiguration config = objectMapper.readValue(jsonConfig, TrustStoreConfiguration.class);

        assertThat(config.getTrustStore()).isNotNull();
    }

    @Test
    public void shouldLoadTrustStoreFromEncodedString() throws IOException {
        byte[] trustStore = Files.readAllBytes(new File(keyStoreRule.getAbsolutePath()).toPath());
        String encodedTrustStore = Base64.getEncoder().encodeToString(trustStore);
        String jsonConfig = "{\"type\": \"encoded\", \"store\": \"" + encodedTrustStore + "\", \"trustStorePassword\": \"" + keyStoreRule.getPassword() + "\"}";
        TrustStoreConfiguration config = objectMapper.readValue(jsonConfig, TrustStoreConfiguration.class);

        assertThat(config.getTrustStore()).isNotNull();
    }

    @Test
    public void shouldDefaultToFileBackedWhenNoTypeProvided() throws IOException {
        String jsonConfig = "{\"trustStorePath\": " + objectMapper.writeValueAsString(keyStoreRule.getAbsolutePath()) + ", \"trustStorePassword\": \"" + keyStoreRule.getPassword() + "\"}";
        TrustStoreConfiguration config = objectMapper.readValue(jsonConfig, TrustStoreConfiguration.class);

        assertThat(config.getTrustStore()).isNotNull();
    }

    @Test
    public void shouldLoadTrustStoreFromFileUsingAliases() throws IOException {
        String jsonConfig = "{\"path\": " + objectMapper.writeValueAsString(keyStoreRule.getAbsolutePath()) + ", \"password\": \"" + keyStoreRule.getPassword() + "\"}";
        TrustStoreConfiguration config = objectMapper.readValue(jsonConfig, TrustStoreConfiguration.class);

        assertThat(config.getTrustStore()).isNotNull();
    }

    @Test(expected = UnrecognizedPropertyException.class)
    public void shouldThrowExceptionWhenIncorrectKeySpecified() throws IOException {
        String jsonConfig = "{\"type\": \"file\", \"trustStorePathhhh\": \"path\", \"trustStorePassword\": \"puppet\"}";
        objectMapper.readValue(jsonConfig, TrustStoreConfiguration.class);
    }
}

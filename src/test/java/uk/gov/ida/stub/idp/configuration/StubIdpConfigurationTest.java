package uk.gov.ida.stub.idp.configuration;

import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.YamlConfigurationFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static io.dropwizard.jackson.Jackson.newObjectMapper;
import static io.dropwizard.jersey.validation.Validators.newValidator;
import static org.hamcrest.core.StringContains.containsString;

public class StubIdpConfigurationTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();
    private final YamlConfigurationFactory factory = new YamlConfigurationFactory<>(
        StubIdpConfiguration.class, newValidator(), newObjectMapper(), "dw.");

    @Test
    public void shouldNotAllowNullValues() throws Exception {
        expectedException.expectMessage(containsString("assertionLifetime may not be null"));
        expectedException.expectMessage(containsString("databaseConfiguration may not be null"));
        expectedException.expectMessage(containsString("europeanIdentity may not be null"));
        expectedException.expectMessage(containsString("metadata may not be null"));
        expectedException.expectMessage(containsString("saml may not be null"));
        expectedException.expectMessage(containsString("serviceInfo may not be null"));
        expectedException.expectMessage(containsString("signingKeyPairConfiguration may not be null"));
        expectedException.expectMessage(containsString("stubIdpYmlFileRefresh may not be null"));
        expectedException.expectMessage(containsString("stubIdpsYmlFileLocation may not be null"));

        factory.build(new StringConfigurationSourceProvider("url: "), "");
    }

    class StringConfigurationSourceProvider implements ConfigurationSourceProvider {
        private String configuration;

        public StringConfigurationSourceProvider(String configuration) {
            this.configuration = configuration;
        }

        @Override
        public InputStream open(String path) throws IOException {
            return new ByteArrayInputStream(this.configuration.getBytes());
        }
    }

}

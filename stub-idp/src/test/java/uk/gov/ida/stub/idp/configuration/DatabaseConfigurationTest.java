package uk.gov.ida.stub.idp.configuration;

import io.dropwizard.configuration.YamlConfigurationFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static io.dropwizard.jackson.Jackson.newObjectMapper;
import static io.dropwizard.jersey.validation.Validators.newValidator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;

public class DatabaseConfigurationTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final YamlConfigurationFactory factory = new YamlConfigurationFactory<>(
            DatabaseConfiguration.class, newValidator(), newObjectMapper(), "dw.");

    @Test
    public void shouldThrowRTEIfNoUrlorVcap() throws Exception {
        expectedException.expectMessage(containsString("Neither url nor vcapServices was workable"));

        DatabaseConfiguration dbCfg = (DatabaseConfiguration) factory.build(
                new StringConfigurationSourceProvider("aField: {}"),
                ""
        );

        dbCfg.getUrl();
    }

    @Test
    public void shouldReturnUrlIfOnlyUrlProvided() throws Exception {
        String url = "jdbc:postgresql://db.example.com:5432/broker?password=dbpassword&ssl=true&user=dbuser";

        DatabaseConfiguration dbCfg = (DatabaseConfiguration) factory.build(
                new StringConfigurationSourceProvider(
                        "url: " + url
                ),
                ""
        );

        assertThat(dbCfg.getUrl()).isEqualTo(url);
    }

    @Test
    public void shouldReturnVcapIfOnlyVcapProvided() throws Exception {
        String url = "jdbc:postgresql://db.example.com:5432/broker?password=dbpassword&ssl=true&user=dbuser";
        String vcap = "{\"postgres\":[{ \"credentials\": { \"jdbcuri\": \"" + url + "\"}}]}";

        DatabaseConfiguration dbCfg = (DatabaseConfiguration) factory.build(
                new StringConfigurationSourceProvider("vcapServices: '" + vcap + "'"), ""
        );

        assertThat(dbCfg.getUrl()).isEqualTo(url);
    }

    @Test
    public void shouldReturnVcapIfBothUrlAndVcapProvided() throws Exception {
        String url = "jdbc:postgresql://db.example.com:5432/broker?password=dbpassword&ssl=true&user=dbuser";
        String vcap = "{\"postgres\":[{ \"credentials\": { \"jdbcuri\": \"" + url + "\"}}]}";

        DatabaseConfiguration dbCfg = (DatabaseConfiguration) factory.build(
                new StringConfigurationSourceProvider("url: idontcare\nvcapServices: '" + vcap + "'"), ""
        );

        assertThat(dbCfg.getUrl()).isEqualTo(url);
    }

    @Test
    public void shouldThrowExceptionIfUrlIsEmpty() throws Exception {
        expectedException.expectMessage(containsString("Neither url nor vcapServices was workable"));

        DatabaseConfiguration dbCfg = (DatabaseConfiguration) factory.build(
                new StringConfigurationSourceProvider("url: ''"), ""
        );

        dbCfg.getUrl();
    }

    @Test
    public void shouldThrowExceptionIfVcapIsEmpty() throws Exception {
        expectedException.expectMessage(containsString("Neither url nor vcapServices was workable"));

        DatabaseConfiguration dbCfg = (DatabaseConfiguration) factory.build(
                new StringConfigurationSourceProvider("vcap: ''"), ""
        );

        dbCfg.getUrl();
    }
}

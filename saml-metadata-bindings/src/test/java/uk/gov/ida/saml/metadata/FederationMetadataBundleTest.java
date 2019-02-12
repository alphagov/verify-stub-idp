package uk.gov.ida.saml.metadata;

import certificates.values.CACertificates;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import keystore.KeyStoreRule;
import keystore.builders.KeyStoreRuleBuilder;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import uk.gov.ida.saml.core.test.OpenSAMLMockitoRunner;
import uk.gov.ida.saml.core.test.TestEntityIds;
import uk.gov.ida.saml.metadata.bundle.MetadataResolverBundle;
import uk.gov.ida.saml.metadata.test.factories.metadata.MetadataFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(OpenSAMLMockitoRunner.class)
public class FederationMetadataBundleTest {
    public static final WireMockRule metadataResource = new WireMockRule(WireMockConfiguration.options().dynamicPort());

    public static KeyStoreRule metadataKeyStoreRule = new KeyStoreRuleBuilder().withCertificate("metadata", CACertificates.TEST_METADATA_CA).withCertificate("root", CACertificates.TEST_ROOT_CA).build();
    public static KeyStoreRule hubKeyStoreRule = new KeyStoreRuleBuilder().withCertificate("hub", CACertificates.TEST_CORE_CA).withCertificate("root", CACertificates.TEST_ROOT_CA).build();
    public static KeyStoreRule idpKeyStoreRule = new KeyStoreRuleBuilder().withCertificate("idp", CACertificates.TEST_IDP_CA).withCertificate("root", CACertificates.TEST_ROOT_CA).build();

    static {
        metadataResource.stubFor(get(urlEqualTo("/metadata")).willReturn(aResponse().withBody(new MetadataFactory().defaultMetadata())));
    }

    public static final DropwizardAppRule<TestConfiguration> APPLICATION_DROPWIZARD_APP_RULE = new DropwizardAppRule<>(
        TestApplication.class,
        ResourceHelpers.resourceFilePath("test-app.yml"),
        ConfigOverride.config("metadata.uri", () -> "http://localhost:" + metadataResource.port() + "/metadata"),
        ConfigOverride.config("metadata.trustStore.path", () -> metadataKeyStoreRule.getAbsolutePath()),
        ConfigOverride.config("metadata.trustStore.password", () -> metadataKeyStoreRule.getPassword()),
        ConfigOverride.config("metadata.unknownProperty", () -> "unknownValue"),
        ConfigOverride.config("metadata.hubTrustStore.path", () -> hubKeyStoreRule.getAbsolutePath()),
        ConfigOverride.config("metadata.hubTrustStore.password", () -> hubKeyStoreRule.getPassword()),
        ConfigOverride.config("metadata.idpTrustStore.path", () -> idpKeyStoreRule.getAbsolutePath()),
        ConfigOverride.config("metadata.idpTrustStore.password", () -> idpKeyStoreRule.getPassword())
    );

    @ClassRule
    public final static RuleChain ruleChain = RuleChain.outerRule(metadataResource)
                                                       .around(metadataKeyStoreRule)
                                                       .around(hubKeyStoreRule)
                                                       .around(idpKeyStoreRule)
                                                       .around(APPLICATION_DROPWIZARD_APP_RULE);

    private static Client client;

    @BeforeClass
    public static void setUp() {
        client = new JerseyClientBuilder(APPLICATION_DROPWIZARD_APP_RULE.getEnvironment()).build(FederationMetadataBundleTest.class.getName() + "2");
    }

    @Test
    public void shouldReadMetadataFromMetadataServerUsingTrustStoreBackedMetadataConfiguration() {
        Response response = client.target("http://localhost:" + APPLICATION_DROPWIZARD_APP_RULE.getLocalPort() +"/foo").request().get();
        assertThat(response.readEntity(String.class)).isEqualTo(TestEntityIds.HUB_ENTITY_ID);
    }

    public static class TestConfiguration extends Configuration {
        @JsonProperty("metadata")
        private MultiTrustStoresBackedMetadataConfiguration metadataConfiguration;

        public Optional<MetadataResolverConfiguration> getMetadataConfiguration() {
            return Optional.ofNullable(metadataConfiguration);
        }
    }

    public static class TestApplication extends Application<TestConfiguration> {
        private MetadataResolverBundle<TestConfiguration> bundle;

        @Override
        public void initialize(Bootstrap<TestConfiguration> bootstrap) {
            super.initialize(bootstrap);
            bundle = new MetadataResolverBundle<>(TestConfiguration::getMetadataConfiguration);
            bootstrap.addBundle(bundle);
        }

        @Override
        public void run(TestConfiguration configuration, Environment environment) {
            environment.jersey().register(new TestResource(bundle.getMetadataResolver()));
        }

        @Path("/")
        public static class TestResource {
            private MetadataResolver metadataResolver;
            TestResource(MetadataResolver metadataResolver) {
                this.metadataResolver = metadataResolver;
            }

            @Path("/foo")
            @GET
            public String getMetadata() throws ResolverException {
                return metadataResolver.resolveSingle(new CriteriaSet(new EntityIdCriterion(TestEntityIds.HUB_ENTITY_ID))).getEntityID();
            };
        }
    }
}

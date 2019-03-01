package uk.gov.ida.apprule.support;

import certificates.values.CACertificates;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import httpstub.HttpStubRule;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import keystore.KeyStoreResource;
import keystore.builders.KeyStoreResourceBuilder;
import org.apache.commons.io.FileUtils;
import org.opensaml.core.config.InitializationService;
import uk.gov.ida.Constants;
import uk.gov.ida.saml.metadata.test.factories.metadata.MetadataFactory;
import uk.gov.ida.stub.idp.StubIdpApplication;
import uk.gov.ida.stub.idp.configuration.IdpStubsConfiguration;
import uk.gov.ida.stub.idp.configuration.StubIdp;
import uk.gov.ida.stub.idp.configuration.StubIdpConfiguration;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.ida.saml.core.test.TestCertificateStrings.STUB_IDP_PUBLIC_PRIMARY_CERT;
import static uk.gov.ida.saml.core.test.TestCertificateStrings.STUB_IDP_PUBLIC_PRIMARY_PRIVATE_KEY;
import static uk.gov.ida.saml.core.test.TestEntityIds.HUB_ENTITY_ID;

public class StubIdpAppRule extends DropwizardAppRule<StubIdpConfiguration> {

    private static final String METADATA_PATH = "/uk/gov/ida/saml/metadata/sp";

    private static final HttpStubRule metadataServer = new HttpStubRule();
    private static final KeyStoreResource trustStore = KeyStoreResourceBuilder.aKeyStoreResource().withCertificate("metadataCA", CACertificates.TEST_METADATA_CA).withCertificate("rootCA", CACertificates.TEST_ROOT_CA).build();
    private static final File STUB_IDPS_FILE = new File(System.getProperty("java.io.tmpdir"), "stub-idps.yml");

    private final List<StubIdp> stubIdps = new ArrayList<>();

    public StubIdpAppRule(ConfigOverride... configOverrides) {
        super(StubIdpApplication.class, "configuration/stub-idp.yml", withDefaultOverrides(configOverrides));
    }

    public static ConfigOverride[] withDefaultOverrides(ConfigOverride ... configOverrides) {
        ImmutableList<ConfigOverride> overrides = ImmutableList.<ConfigOverride>builder()
                .add(ConfigOverride.config("metadata.uri", "http://localhost:" + metadataServer.getPort() + METADATA_PATH))
                .add(ConfigOverride.config("hubEntityId", HUB_ENTITY_ID))
                .add(ConfigOverride.config("basicAuthEnabledForUserResource", "true"))
                .add(ConfigOverride.config("server.requestLog.appenders[0].type", "console"))
                .add(ConfigOverride.config("server.requestLog.appenders[1].type", "console"))
                .add(ConfigOverride.config("server.requestLog.appenders[2].type", "console"))
                .add(ConfigOverride.config("server.applicationConnectors[0].port", "0"))
                .add(ConfigOverride.config("server.adminConnectors[0].port", "0"))
                .add(ConfigOverride.config("logging.appenders[0].type", "console"))
                .add(ConfigOverride.config("logging.appenders[1].type", "console"))
                .add(ConfigOverride.config("logging.appenders[2].type", "console"))
                .add(ConfigOverride.config("stubIdpsYmlFileLocation", STUB_IDPS_FILE.getAbsolutePath()))
                .add(ConfigOverride.config("metadata.trustStore.store", trustStore.getAbsolutePath()))
                .add(ConfigOverride.config("metadata.trustStore.password", trustStore.getPassword()))
                .add(ConfigOverride.config("europeanIdentity.enabled", "true"))
                .add(ConfigOverride.config("europeanIdentity.hubConnectorEntityId", HUB_ENTITY_ID))
                .add(ConfigOverride.config("europeanIdentity.stubCountryBaseUrl", "http://localhost:0"))
                .add(ConfigOverride.config("europeanIdentity.metadata.uri", "http://localhost:" + metadataServer.getPort() + METADATA_PATH))
                .add(ConfigOverride.config("europeanIdentity.metadata.expectedEntityId", HUB_ENTITY_ID))
                .add(ConfigOverride.config("europeanIdentity.metadata.trustStore.store", trustStore.getAbsolutePath()))
                .add(ConfigOverride.config("europeanIdentity.metadata.trustStore.password", trustStore.getPassword()))
                .add(ConfigOverride.config("signingKeyPairConfiguration.privateKeyConfiguration.type", "encoded"))
                .add(ConfigOverride.config("signingKeyPairConfiguration.privateKeyConfiguration.key", STUB_IDP_PUBLIC_PRIMARY_PRIVATE_KEY))
                .add(ConfigOverride.config("signingKeyPairConfiguration.publicKeyConfiguration.type", "x509"))
                .add(ConfigOverride.config("signingKeyPairConfiguration.publicKeyConfiguration.cert", STUB_IDP_PUBLIC_PRIMARY_CERT))
                .add(ConfigOverride.config("europeanIdentity.signingKeyPairConfiguration.privateKeyConfiguration.type", "encoded"))
                .add(ConfigOverride.config("europeanIdentity.signingKeyPairConfiguration.privateKeyConfiguration.key", STUB_IDP_PUBLIC_PRIMARY_PRIVATE_KEY))
                .add(ConfigOverride.config("europeanIdentity.signingKeyPairConfiguration.publicKeyConfiguration.type", "x509"))
                .add(ConfigOverride.config("europeanIdentity.signingKeyPairConfiguration.publicKeyConfiguration.cert", STUB_IDP_PUBLIC_PRIMARY_CERT))
                .add(ConfigOverride.config("database.url", "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"))
                .add(ConfigOverride.config("singleIdpJourney.enabled", "true"))
                .add(configOverrides)
                .build();
        return overrides.toArray(new ConfigOverride[0]);
    }

    @Override
    public void before() {
        trustStore.create();

        IdpStubsConfiguration idpStubsConfiguration = new TestIdpStubsConfiguration(stubIdps);
        try {
            FileUtils.write(STUB_IDPS_FILE, new ObjectMapper().writeValueAsString(idpStubsConfiguration));
            STUB_IDPS_FILE.deleteOnExit();

            InitializationService.initialize();

            metadataServer.reset();
            metadataServer.register(METADATA_PATH, 200, Constants.APPLICATION_SAMLMETADATA_XML, new MetadataFactory().defaultMetadata());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        super.before();
    }

    @Override
    protected void after() {
        trustStore.delete();
        STUB_IDPS_FILE.delete();

        super.after();
    }

    public StubIdpAppRule withStubIdp(StubIdp stubIdp) {
        this.stubIdps.add(stubIdp);
        return this;
    }

    public URI getMetadataPath() {
        return URI.create("http://localhost:" + metadataServer.getPort() + METADATA_PATH);
    }

    private class TestIdpStubsConfiguration extends IdpStubsConfiguration {
        public TestIdpStubsConfiguration(List<StubIdp> idps) {
            this.stubIdps = idps;
        }
    }
}

package uk.gov.ida.saml.metadata;

import com.codahale.metrics.health.HealthCheck;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import uk.gov.ida.saml.metadata.test.factories.metadata.MetadataFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class MetadataHealthCheckTest {

    public static final String EXPECTED_ENTITY_ID = "https://signin.service.gov.uk";

    @BeforeClass
    public static void bootStrapOpenSAML() throws InitializationException {
        InitializationService.initialize();
    }

    @Test
    public void shouldReturnHealthyResponseWhenMetadataContainsHubEntityID() throws Exception {
        String metadata = new MetadataFactory().defaultMetadata();

        StringBackedMetadataResolver filesystemMetadataResolver = new StringBackedMetadataResolver(metadata);
        initializeResolver(filesystemMetadataResolver);
        MetadataHealthCheck metadataHealthCheck = new MetadataHealthCheck(filesystemMetadataResolver, EXPECTED_ENTITY_ID);

        HealthCheck.Result result = metadataHealthCheck.check();

        assertThat(result.isHealthy()).isTrue();
    }

    @Test
    public void shouldReturnUnhealthyResponseWhenHubEntityCannotBeFound() throws Exception {
        String metadata = new MetadataFactory().emptyMetadata();
        StringBackedMetadataResolver filesystemMetadataResolver = new StringBackedMetadataResolver(metadata);
        initializeResolver(filesystemMetadataResolver);
        MetadataHealthCheck metadataHealthCheck = new MetadataHealthCheck(filesystemMetadataResolver, EXPECTED_ENTITY_ID);

        HealthCheck.Result result = metadataHealthCheck.check();

        assertThat(result.isHealthy()).isFalse();
    }

    private void initializeResolver(StringBackedMetadataResolver filesystemMetadataResolver) throws ComponentInitializationException {
        BasicParserPool parserPool = new BasicParserPool();
        parserPool.initialize();
        filesystemMetadataResolver.setParserPool(parserPool);
        filesystemMetadataResolver.setId("test resolver");
        filesystemMetadataResolver.initialize();
    }
}

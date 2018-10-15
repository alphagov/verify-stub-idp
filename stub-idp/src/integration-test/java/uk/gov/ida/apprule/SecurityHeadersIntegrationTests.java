package uk.gov.ida.apprule;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.apprule.support.IntegrationTestHelper;
import uk.gov.ida.apprule.support.StubIdpAppRule;
import uk.gov.ida.stub.idp.filters.SecurityHeadersFilterTest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityHeadersIntegrationTests extends IntegrationTestHelper {

    private final Client client = JerseyClientBuilder.createClient().property(ClientProperties.FOLLOW_REDIRECTS, false);

    @ClassRule
    public static final StubIdpAppRule applicationRule = new StubIdpAppRule();

    @Before
    public void refreshMetadata() {
        client.target("http://localhost:"+applicationRule.getAdminPort()+"/tasks/connector-metadata-refresh").request().post(Entity.text(""));
    }

    @Test
    public void securityHeaderTest() {
        final Response response = client.target(UriBuilder.fromUri("http://localhost:" + applicationRule.getLocalPort())
                .path("/page_does_not_exist")
                .build())
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(404);
        SecurityHeadersFilterTest.checkSecurityHeaders(response.getHeaders());
    }

}

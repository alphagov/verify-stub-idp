package uk.gov.ida.apprule;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.apprule.steps.AuthnRequestSteps;
import uk.gov.ida.apprule.support.IntegrationTestHelper;
import uk.gov.ida.apprule.support.StubIdpAppRule;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieNames;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.stub.idp.Urls.UNKNOWN_HINTS_PARAM;
import static uk.gov.ida.stub.idp.builders.StubIdpBuilder.aStubIdp;

public class AutoEscapingIntegrationTest extends IntegrationTestHelper {

    private static final String IDP_NAME = "auto-escaping-idp";
    private static final String DISPLAY_NAME = "Auto-Escaping Identity Service";

    private final Client client = JerseyClientBuilder.createClient().property(ClientProperties.FOLLOW_REDIRECTS, false);
    private final AuthnRequestSteps authnRequestSteps = new AuthnRequestSteps(
            client,
            IDP_NAME,
            applicationRule.getLocalPort());

    @ClassRule
    public static final StubIdpAppRule applicationRule = new StubIdpAppRule()
            .withStubIdp(aStubIdp().withId(IDP_NAME).withDisplayName(DISPLAY_NAME).build());

    @Before
    public void before() {
        client.target("http://localhost:" + applicationRule.getAdminPort() + "/tasks/metadata-refresh").request().post(Entity.text(""));
    }

    @Test
    public void userHasAnXSSHintAndItIsCorrectlyEscaped() {
        final String xss = "afd5j\"><script>alert(\"pwnage\")</script>c3tw";
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsAuthnRequestToStubIdp(xss);
        final String response = userSeesTheHintOnTheDebugPage(cookies, xss);
        assertThat(response).doesNotContain(xss);
    }

    private String userSeesTheHintOnTheDebugPage(AuthnRequestSteps.Cookies cookies, String hint) {
        Response response = client.target(UriBuilder.fromUri("http://localhost:" + applicationRule.getLocalPort()).path(Urls.IDP_DEBUG_RESOURCE).build(IDP_NAME))
                .queryParam(UNKNOWN_HINTS_PARAM, hint)
                .request()
                .cookie(CookieNames.SESSION_COOKIE_NAME, cookies.getSessionId())
                .cookie(CookieNames.SECURE_COOKIE_NAME, cookies.getSecure())
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        return response.readEntity(String.class);
    }

}

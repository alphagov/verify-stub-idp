package uk.gov.ida.apprule;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.apprule.steps.AuthnRequestSteps;
import uk.gov.ida.apprule.support.StubIdpAppRule;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieNames;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.stub.idp.builders.StubIdpBuilder.aStubIdp;

public class EmojiSupportIntegrationTests {

    private static final String IDP_NAME = "stub-idp-one";
    private static final String DISPLAY_NAME = "Emoji Identity Service";

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
        client.target("http://localhost:"+applicationRule.getAdminPort()+"/tasks/metadata-refresh").request().post(Entity.text(""));
    }

    @Test
    public void loginBehaviourTest() {
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsAuthnRequestToStubIdp();
        authnRequestSteps.userLogsIn(cookies, IDP_NAME+"-emoji");
        final Document page = userConsents(cookies);
        assertThat(page.getElementById("firstName").text()).isEqualTo("😀");
        // can't do a direct comparison of the complete displayed text using jsoup
        assertThat(page.getElementById("address").text()).contains("🏠");
        assertThat(page.getElementById("address").text()).contains("🏘");
    }

    private Document userConsents(AuthnRequestSteps.Cookies cookies) {
        Response response = aStubIdpRequest(Urls.CONSENT_RESOURCE, cookies).get();

        assertThat(response.getStatus()).isEqualTo(200);

        final Document page = Jsoup.parse(response.readEntity(String.class));
        assertThat(page.getElementsByTag("title").text()).isEqualTo(format("Consent page for {0}", DISPLAY_NAME));

        return page;
    }

    private Invocation.Builder aStubIdpRequest(String path, AuthnRequestSteps.Cookies cookies) {
        return client.target(authnRequestSteps.getStubIdpUri(path))
                .request()
                .cookie(CookieNames.SESSION_COOKIE_NAME, cookies.getSessionId())
                .cookie(CookieNames.SECURE_COOKIE_NAME, cookies.getSecure());
    }

}

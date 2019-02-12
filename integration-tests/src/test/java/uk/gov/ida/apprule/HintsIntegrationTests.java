package uk.gov.ida.apprule;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.apprule.steps.AuthnRequestSteps;
import uk.gov.ida.apprule.support.IntegrationTestHelper;
import uk.gov.ida.apprule.support.StubIdpAppRule;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieNames;
import uk.gov.ida.stub.idp.domain.IdpHint;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.stub.idp.builders.StubIdpBuilder.aStubIdp;

public class HintsIntegrationTests extends IntegrationTestHelper {

    private static final String IDP_NAME = "stub-idp-one";
    private static final String DISPLAY_NAME = "Hints Identity Service";

    private AuthnRequestSteps authnRequestSteps;

    @ClassRule
    public static final StubIdpAppRule applicationRule = new StubIdpAppRule()
            .withStubIdp(aStubIdp().withId(IDP_NAME).withDisplayName(DISPLAY_NAME).build());

    public Client client = JerseyClientBuilder.createClient().property(ClientProperties.FOLLOW_REDIRECTS, false);

    @Before
    public void setUp() {
        client.target("http://localhost:" + applicationRule.getAdminPort() + "/tasks/metadata-refresh").request().post(Entity.text(""));
        authnRequestSteps = new AuthnRequestSteps(
                client,
                IDP_NAME,
                applicationRule.getLocalPort());
    }

    @Test
    public void debugPageShowsHints() throws Exception {
        List<String> hints = ImmutableList.of(IdpHint.has_apps.name(), "snakes", "plane");
        final Optional<Boolean> registration = Optional.of(true);
        final Optional<String> language = Optional.absent();
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsAuthnRequestToStubIdp(hints, language, registration);
        Response response = aUserVisitsTheDebugPage(IDP_NAME, cookies);

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        Document doc = Jsoup.parse(response.readEntity(String.class));
        assertThat(getListItems(doc, "known-hints")).containsExactly(IdpHint.has_apps.name());
        assertThat(getListItems(doc, "unknown-hints")).containsExactlyInAnyOrder("snakes", "plane");

        assertThat(doc.getElementById("language-hint").text()).isEqualTo("No language hint was set.");

        assertThat(doc.getElementById("registration").text()).isEqualTo("\"registration\" hint is \"true\"");
    }

    @Test
    public void debugPageShowsLanguageHint() throws Exception {
        List<String> hints = ImmutableList.of();
        final Optional<Boolean> registration = Optional.absent();
        final Optional<String> language = Optional.fromNullable("cy");
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsAuthnRequestToStubIdp(hints, language, registration);
        Response response = aUserVisitsTheDebugPage(IDP_NAME, cookies);

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        Document doc = Jsoup.parse(response.readEntity(String.class));
        Element languageHintElement = doc.getElementById("language-hint");
        assertThat(languageHintElement).isNotNull();
        assertThat(languageHintElement.text()).contains("\"cy\"");

        assertThat(doc.getElementById("registration").text()).isEqualTo("\"registration\" hint not received");
    }

    private List<String> getListItems(Document doc, String parentClass) {
        return doc.getElementsByClass(parentClass).stream()
                .flatMap(ul -> ul.getElementsByTag("li").stream())
                .map(Element::text).collect(Collectors.toList());
    }

    public Response aUserVisitsTheDebugPage(String idp, AuthnRequestSteps.Cookies cookies) {
        return client.target(getDebugPath(idp))
                .request()
                .cookie(CookieNames.SESSION_COOKIE_NAME, cookies.getSessionId())
                .cookie(CookieNames.SECURE_COOKIE_NAME, cookies.getSecure())
                .get();
    }

    private String getDebugPath(String idp) {
        UriBuilder uriBuilder = UriBuilder.fromPath("http://localhost:"+applicationRule.getLocalPort()+Urls.DEBUG_RESOURCE.replace("{idpId}", IDP_NAME));
        return uriBuilder.build(idp).toASCIIString();
    }

}

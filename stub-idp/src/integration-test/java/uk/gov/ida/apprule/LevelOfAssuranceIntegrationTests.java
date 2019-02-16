package uk.gov.ida.apprule;

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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.stub.idp.builders.StubIdpBuilder.aStubIdp;

public class LevelOfAssuranceIntegrationTests extends IntegrationTestHelper {

    public static final String IDP_NAME = "loa-idp";
    public static final String DISPLAY_NAME = "Level Of Assurance Identity Service";

    private final Client client = JerseyClientBuilder.createClient().property(ClientProperties.FOLLOW_REDIRECTS, false);
    private final AuthnRequestSteps authnRequestSteps = new AuthnRequestSteps(
            client,
            IDP_NAME,
            applicationRule.getLocalPort());

    @ClassRule
    public static final StubIdpAppRule applicationRule = new StubIdpAppRule()
            .withStubIdp(aStubIdp().withId(IDP_NAME).withDisplayName(DISPLAY_NAME).build());

    @Before
    public void refreshMetadata() {
        client.target("http://localhost:"+applicationRule.getAdminPort()+"/tasks/metadata-refresh").request().post(Entity.text(""));
    }

    @Test
    public void debugPageShowsAuthnContextsAndComparisonType() {
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsAuthnRequestToStubIdp("hint");

        Response response = aUserVisitsTheDebugPage(IDP_NAME, cookies);

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        Document doc = Jsoup.parse(response.readEntity(String.class));
        assertThat(doc.getElementById("authn-request-comparision-type").text()).isEqualTo("AuthnRequest comparison type is \"minimum\".");
        assertThat(getListItems(doc, "authn-contexts")).containsExactly("LEVEL_1", "LEVEL_2");
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
        UriBuilder uriBuilder = UriBuilder.fromPath("http://localhost:"+applicationRule.getLocalPort()+Urls.DEBUG_RESOURCE);
        return uriBuilder.build(idp).toASCIIString();
    }

}

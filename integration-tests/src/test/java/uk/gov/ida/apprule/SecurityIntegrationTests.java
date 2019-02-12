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
import uk.gov.ida.stub.idp.filters.SecurityHeadersFilterTest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.util.Objects;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.stub.idp.builders.StubIdpBuilder.aStubIdp;
import static uk.gov.ida.stub.idp.csrf.CSRFCheckProtectionFilter.CSRF_PROTECT_FORM_KEY;

public class SecurityIntegrationTests extends IntegrationTestHelper {

    private static final String IDP_NAME = "stub-idp-one";
    private static final String DISPLAY_NAME = "User Login Identity Service";
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

    @Test
    public void csrfTokenIsUniquePerPageLoad() {
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsAuthnRequestToStubIdp();
        String csrfValueOne = getLoginPageCsrfValue(cookies);
        String csrfValueTwo = getLoginPageCsrfValue(cookies);
        assertThat(csrfValueOne).isNotEqualTo(csrfValueTwo);
    }

    @Test
    public void whenCsrfTokenIsModifiedThenRequestDoesNotWork() {
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsAuthnRequestToStubIdp();
        getLoginPageCsrfValue(cookies);

        Form form = new Form();
        form.param(Urls.USERNAME_PARAM, IDP_NAME);
        form.param(Urls.PASSWORD_PARAM, "bar");
        form.param(Urls.SUBMIT_PARAM, "SignIn");
        form.param(CSRF_PROTECT_FORM_KEY, "this_is_not_a_csrf_value");

        Response response = client.target(authnRequestSteps.getStubIdpUri(Urls.LOGIN_RESOURCE))
                .request()
                .cookie(CookieNames.SESSION_COOKIE_NAME, cookies.getSessionId())
                .cookie(CookieNames.SECURE_COOKIE_NAME, cookies.getSecure())
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertThat(response.getStatus()).isEqualTo(500);
    }

    private String getLoginPageCsrfValue(AuthnRequestSteps.Cookies cookies) {
        Response response = client.target(authnRequestSteps.getStubIdpUri(Urls.LOGIN_RESOURCE))
                .request()
                .cookie(CookieNames.SESSION_COOKIE_NAME, cookies.getSessionId())
                .cookie(CookieNames.SECURE_COOKIE_NAME, cookies.getSecure())
                .get();

        assertThat(response.getStatus()).isEqualTo(200);

        final Document entity = Jsoup.parse(response.readEntity(String.class));
        final Element csrfElement = entity.getElementById(CSRF_PROTECT_FORM_KEY);
        if(!Objects.isNull(csrfElement)) {
            return entity.getElementById(CSRF_PROTECT_FORM_KEY).val();
        }
        return null;
    }

}

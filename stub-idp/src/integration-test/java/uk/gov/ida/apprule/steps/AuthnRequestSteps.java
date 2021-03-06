package uk.gov.ida.apprule.steps;

import com.google.common.collect.ImmutableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import uk.gov.ida.apprule.support.TestSamlRequestFactory;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieNames;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthnRequestSteps {
    private final Client client;
    private final String idpName;
    private int port;

    public static class Cookies {
        private final String sessionId;
        private final String secure;

        public Cookies(String sessionId, String secure) {
            this.sessionId = sessionId;
            this.secure = secure;
        }

        public String getSessionId() {
            return sessionId;
        }

        public String getSecure() {
            return secure;
        }
    }

    public AuthnRequestSteps(Client client, String idpName, int port) {
        this.client = client;
        this.idpName = idpName;
        this.port = port;
    }

    public Cookies userPostsAuthnRequestToStubIdp() {
        return userPostsAuthnRequestToStubIdp(ImmutableList.of(), Optional.empty(), Optional.empty());
    }

    public Cookies userPostsAuthnRequestToStubIdp(String hint) {
        return userPostsAuthnRequestToStubIdp(ImmutableList.of(hint), Optional.empty(), Optional.empty());
    }

    public Cookies userPostsAuthnRequestToStubIdp(List<String> hints, Optional<String> language, Optional<Boolean> registration) {
        String authnRequest = TestSamlRequestFactory.anAuthnRequest();
        Response response = postAuthnRequest(hints, language, registration, authnRequest, Urls.IDP_SAML2_SSO_RESOURCE);

        assertThat(response.getStatus()).isEqualTo(303);
        if(registration.isPresent() && registration.get()) {
            assertThat(response.getLocation().getPath()).startsWith(getStubIdpUri(Urls.REGISTER_RESOURCE).getPath());
        } else {
            assertThat(response.getLocation().getPath()).startsWith(getStubIdpUri(Urls.LOGIN_RESOURCE).getPath());
        }

        return getCookiesAndFollowRedirect(response);
    }

    private Cookies getCookiesAndFollowRedirect(Response response) {
        final NewCookie sessionCookie = response.getCookies().get(CookieNames.SESSION_COOKIE_NAME);
        assertThat(sessionCookie).isNotNull();
        assertThat(sessionCookie.getValue()).isNotNull();
        final String sessionCookieValue = sessionCookie.getValue();

        final NewCookie secureCookie = response.getCookies().get(CookieNames.SECURE_COOKIE_NAME);
        final String secureCookieValue = secureCookie==null?null:secureCookie.getValue();

        response = client.target(response.getLocation())
                .request()
                .cookie(CookieNames.SESSION_COOKIE_NAME, sessionCookieValue)
                .cookie(CookieNames.SECURE_COOKIE_NAME, secureCookieValue)
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        return new Cookies(sessionCookieValue, secureCookieValue);
    }

    private Response postAuthnRequest(List<String> hints, Optional<String> language, Optional<Boolean> registration, String authnRequest, String ssoEndpoint) {
        Form form = new Form();
        form.param(Urls.SAML_REQUEST_PARAM, authnRequest);
        registration.ifPresent(aBoolean -> form.param(Urls.REGISTRATION_PARAM, aBoolean.toString()));
        language.ifPresent(s -> form.param(Urls.LANGUAGE_HINT_PARAM, s));
        for(String hint : hints) {
            form.param(Urls.HINTS_PARAM, hint);
        }
        form.param(Urls.RELAY_STATE_PARAM, "relay_state");

        return client.target(getStubIdpUri(ssoEndpoint))
                .request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
    }

    public void userLogsIn(Cookies cookies) {
        userLogsIn(cookies, idpName);
    }

    public void userLogsIn(Cookies cookies, String username) {
        userLogsIn(cookies, username, Urls.LOGIN_RESOURCE, Urls.CONSENT_RESOURCE);
    }

    private void userLogsIn(Cookies cookies, String username, String loginUrl, String consentUrl) {
        Form form = new Form();
        form.param(Urls.USERNAME_PARAM, username);
        form.param(Urls.PASSWORD_PARAM, "bar");
        form.param(Urls.SUBMIT_PARAM, "SignIn");

        Response response = client.target(getStubIdpUri(loginUrl))
                .request()
                .cookie(CookieNames.SESSION_COOKIE_NAME, cookies.getSessionId())
                .cookie(CookieNames.SECURE_COOKIE_NAME, cookies.getSecure())
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertThat(response.getStatus()).isEqualTo(303);
        assertThat(response.getLocation().getPath()).isEqualTo(getStubIdpUri(consentUrl).getPath());
    }

    public String userConsentsReturnSamlResponse(Cookies cookies, boolean randomize) {
        return userConsentsReturnSamlResponse(cookies, randomize, Urls.CONSENT_RESOURCE);
    }

    private String userConsentsReturnSamlResponse(Cookies cookies, boolean randomize, String consentUrl) {
        Response response = client.target(getStubIdpUri(consentUrl))
                .request()
                .cookie(CookieNames.SESSION_COOKIE_NAME, cookies.getSessionId())
                .cookie(CookieNames.SECURE_COOKIE_NAME, cookies.getSecure())
                .get();

        assertThat(response.getStatus()).isEqualTo(200);

        Form form = new Form();
        form.param(Urls.SUBMIT_PARAM, "I Agree");
        form.param(Urls.RANDOMISE_PID_PARAM, Boolean.toString(randomize));

        response = client.target(getStubIdpUri(consentUrl))
                .request()
                .cookie(CookieNames.SESSION_COOKIE_NAME, cookies.getSessionId())
                .cookie(CookieNames.SECURE_COOKIE_NAME, cookies.getSecure())
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertThat(response.getStatus()).isEqualTo(200);
        final Document page = Jsoup.parse(response.readEntity(String.class));
        assertThat(page.getElementsByTag("title").text()).isEqualTo("Saml Processing...");

        return page.getElementsByAttributeValue("name", "SAMLResponse").val();
    }

    public void userViewsTheDebugPage(Cookies cookies) {
        userViewsTheDebugPage(cookies, getStubIdpUri(Urls.DEBUG_RESOURCE));
    }

    private String userViewsTheDebugPage(Cookies cookies, URI debugUrl) {
        Response response = client.target(debugUrl)
                .request()
                .cookie(CookieNames.SESSION_COOKIE_NAME, cookies.getSessionId())
                .cookie(CookieNames.SECURE_COOKIE_NAME, cookies.getSecure())
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        // we shoud probably test more things
        final String page = response.readEntity(String.class);
        assertThat(page).contains(cookies.getSessionId());
        return page;
    }

    public URI getStubIdpUri(String path) {
        return UriBuilder.fromUri("http://localhost:" + port)
                .path(path)
                .build(idpName);
    }
}

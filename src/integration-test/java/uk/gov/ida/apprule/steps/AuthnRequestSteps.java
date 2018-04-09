package uk.gov.ida.apprule.steps;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import uk.gov.ida.apprule.support.TestSamlRequestFactory;
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
        return userPostsAuthnRequestToStubIdp(ImmutableList.of(), Optional.absent(), Optional.absent());
    }

    public Cookies userPostsAuthnRequestToStubIdp(String hint) {
        return userPostsAuthnRequestToStubIdp(ImmutableList.of(hint), Optional.absent(), Optional.absent());
    }

    public Cookies userPostsAuthnRequestToStubIdp(List<String> hints, Optional<String> language, Optional<Boolean> registration) {
        Form form = new Form();
        form.param(Urls.SAML_REQUEST_PARAM, TestSamlRequestFactory.anAuthnRequest());
        if(registration.isPresent()) {
            form.param(Urls.REGISTRATION_PARAM, registration.get().toString());
        }
        if(language.isPresent()) {
            form.param(Urls.LANGUAGE_HINT_PARAM, language.get());
        }
        for(String hint : hints) {
            form.param(Urls.HINTS_PARAM, hint);
        }
        form.param(Urls.RELAY_STATE_PARAM, "relay_state");

        Response response = client.target(getStubIdpUri(Urls.IDP_SAML2_SSO_RESOURCE))
                .request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertThat(response.getStatus()).isEqualTo(303);
        if(registration.isPresent() && registration.get()) {
            assertThat(response.getLocation().getPath()).startsWith(getStubIdpUri(Urls.REGISTER_RESOURCE).getPath());
        } else {
            assertThat(response.getLocation().getPath()).startsWith(getStubIdpUri(Urls.LOGIN_RESOURCE).getPath());
        }

        final NewCookie sessionCookie = response.getCookies().get(CookieNames.SESSION_COOKIE_NAME);
        assertThat(sessionCookie).isNotNull();
        assertThat(sessionCookie.getValue()).isNotNull();
        final NewCookie secureCookie = response.getCookies().get(CookieNames.SECURE_COOKIE_NAME);
        final String secureCookieValue = secureCookie==null?null:secureCookie.getValue();

        response = client.target(response.getLocation())
                .request()
                .cookie(CookieNames.SESSION_COOKIE_NAME, sessionCookie.getValue())
                .cookie(CookieNames.SECURE_COOKIE_NAME, secureCookieValue)
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        return new Cookies(sessionCookie.getValue(), secureCookieValue);
    }

    public void userLogsIn(Cookies cookies) {
        userLogsIn(cookies, idpName);
    }

    public void userLogsIn(Cookies cookies, String username) {
        Form form = new Form();
        form.param(Urls.USERNAME_PARAM, username);
        form.param(Urls.PASSWORD_PARAM, "bar");
        form.param(Urls.SUBMIT_PARAM, "SignIn");

        Response response = client.target(getStubIdpUri(Urls.LOGIN_RESOURCE))
                .request()
                .cookie(CookieNames.SESSION_COOKIE_NAME, cookies.getSessionId())
                .cookie(CookieNames.SECURE_COOKIE_NAME, cookies.getSecure())
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertThat(response.getStatus()).isEqualTo(303);
        assertThat(response.getLocation().getPath()).isEqualTo(getStubIdpUri(Urls.CONSENT_RESOURCE).getPath());
    }

    public String userConsentsReturnSamlResponse(Cookies cookies, boolean randomize) {
        Response response = client.target(getStubIdpUri(Urls.CONSENT_RESOURCE))
                .request()
                .cookie(CookieNames.SESSION_COOKIE_NAME, cookies.getSessionId())
                .cookie(CookieNames.SECURE_COOKIE_NAME, cookies.getSecure())
                .get();

        assertThat(response.getStatus()).isEqualTo(200);

        Form form = new Form();
        form.param(Urls.SUBMIT_PARAM, "I Agree");
        form.param(Urls.RANDOMISE_PID_PARAM, Boolean.toString(randomize));

        response = client.target(getStubIdpUri(Urls.CONSENT_RESOURCE))
                .request()
                .cookie(CookieNames.SESSION_COOKIE_NAME, cookies.getSessionId())
                .cookie(CookieNames.SECURE_COOKIE_NAME, cookies.getSecure())
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertThat(response.getStatus()).isEqualTo(200);
        final Document page = Jsoup.parse(response.readEntity(String.class));
        assertThat(page.getElementsByTag("title").text()).isEqualTo("Saml Processing...");

        return page.getElementsByAttributeValue("name", "SAMLResponse").val();
    }

    public URI getStubIdpUri(String path) {
        return UriBuilder.fromUri("http://localhost:" + port)
                .path(path)
                .build(idpName);
    }
}

package uk.gov.ida.apprule.steps;

import sun.security.provider.certpath.OCSPResponse;
import uk.gov.ida.apprule.PreRegistrationIntegrationTest;
import uk.gov.ida.apprule.support.StubIdpAppRule;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class PreRegistrationSteps {
    private Response response;
    private Cookies cookies;
    private Client client;
    private StubIdpAppRule applicationRule;

    private static final String IDP_NAME = "stub-idp-one";
    private static final String DISPLAY_NAME = "Stub Idp One Pre-Register";

    public PreRegistrationSteps(Client client, StubIdpAppRule applicationRule) {
        this.client = client;
        this.applicationRule = applicationRule;
        this.cookies = new Cookies();
    }

    public PreRegistrationSteps userNavigatesTo(String path) {
        this.response = client.target(getUri(IDP_NAME + path))
                .request()
                .cookie(cookies.getSessionCookie())
                .get();
        cookies.extractCookies(response);
        return this;
    }

    public PreRegistrationSteps userSuccessfullyNavigatesTo(String path) {
        this.response = client.target(getUri(IDP_NAME + path))
                .request()
                .cookie(cookies.getSessionCookie())
                .get();
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        cookies.extractCookies(response);
        return this;
    }

    public PreRegistrationSteps userIsRedirectedTo(String path) {
        return userIsRedirectedTo(getUri(IDP_NAME + path));
    }

    public PreRegistrationSteps userIsRedirectedTo(URI uri) {
        assertThat(response.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
        assertThat(response.getLocation()).isEqualTo(uri);
        return this;
    }

    public PreRegistrationSteps theRedirectIsFollowed() {
        response = client.target(response.getLocation())
                .request()
                .cookie(cookies.getSessionCookie())
                .get();
        cookies.extractCookies(response);
        return this;
    }

    public PreRegistrationSteps theResponseStatusIs(Response.Status status) {
        assertThat(response.getStatus()).isEqualTo(status.getStatusCode());
        return this;
    }

    public PreRegistrationSteps userSubmitsForm(Form form, String path) {
        return postFormTo(form, path);
    }

    public PreRegistrationSteps userSubmitsFormTo(Form form, URI uri) {
        return postFormTo(form, uri);
    }

    public PreRegistrationSteps clientPostsFormData(Form form, String path) {
        return postFormTo(form, path);
    }

    private PreRegistrationSteps postFormTo(Form form, String path) {
        return postFormTo(form, getUri(IDP_NAME + path));
    }

    private PreRegistrationSteps postFormTo(Form form, URI uri){
        response = client.target(uri)
                .request()
                .cookie(cookies.getSessionCookie())
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        cookies.extractCookies(response);
        return this;
    }

    public PreRegistrationSteps responseContains(String ... content) {
        String entityString = response.readEntity(String.class);
        Arrays.stream(content).forEach(string -> assertThat(entityString).contains(string));
        return this;
    }

    public Cookies getCookies() {
        return this.cookies;
    }

    private URI getUri(String path) {
        return UriBuilder.fromUri("http://localhost:" + applicationRule.getLocalPort()).path(path).build();
    }
}

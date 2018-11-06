package uk.gov.ida.apprule;

import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import uk.gov.ida.apprule.steps.AuthnRequestSteps;
import uk.gov.ida.apprule.support.IntegrationTestHelper;
import uk.gov.ida.apprule.support.SamlDecrypter;
import uk.gov.ida.apprule.support.StubIdpAppRule;
import uk.gov.ida.saml.hub.domain.InboundResponseFromIdp;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieNames;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.cert.CertificateException;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.common.HttpHeaders.CACHE_CONTROL_KEY;
import static uk.gov.ida.common.HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE;
import static uk.gov.ida.common.HttpHeaders.PRAGMA_KEY;
import static uk.gov.ida.common.HttpHeaders.PRAGMA_NO_CACHE_VALUE;
import static uk.gov.ida.stub.idp.builders.StubIdpBuilder.aStubIdp;

public class UserLogsInIntegrationTests extends IntegrationTestHelper {

    // Use stub-idp-one as it allows us to use the defaultMetadata in MetadataFactory
    private static final String IDP_NAME = "stub-idp-one";
    private static final String DISPLAY_NAME = "User Login Identity Service";

    private final Client client = JerseyClientBuilder.createClient().property(ClientProperties.FOLLOW_REDIRECTS, false);
    private final AuthnRequestSteps authnRequestSteps = new AuthnRequestSteps(
            client,
            IDP_NAME,
            applicationRule.getLocalPort());
    private final SamlDecrypter samlDecrypter = new SamlDecrypter(client, applicationRule.getMetadataPath(), applicationRule.getConfiguration().getHubEntityId(), applicationRule.getLocalPort(), empty());

    @ClassRule
    public static final StubIdpAppRule applicationRule = new StubIdpAppRule()
            .withStubIdp(aStubIdp().withId(IDP_NAME).withDisplayName(DISPLAY_NAME).build());

    @Before
    public void refreshMetadata() {
        client.target("http://localhost:"+applicationRule.getAdminPort()+"/tasks/metadata-refresh").request().post(Entity.text(""));
    }

    @Test
    public void loginBehaviourTest() {
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsAuthnRequestToStubIdp();
        authnRequestSteps.userLogsIn(cookies);
        authnRequestSteps.userConsentsReturnSamlResponse(cookies, false);
    }

    @Test
    @Ignore
    public void testStaleSessionReaper() throws InterruptedException {
        // set times in StaleSessionReaperConfiguration to 1s, run this test and check the log lines
        for(int i=0;i<10;i++) {
            authnRequestSteps.userPostsAuthnRequestToStubIdp();
            Thread.sleep(1000);
        }
    }

    @Test
    public void debugPageLoadsTest() {
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsAuthnRequestToStubIdp();
        authnRequestSteps.userLogsIn(cookies);
        authnRequestSteps.userViewsTheDebugPage(cookies);
    }

    @Test
    public void sessionIdRequiredAlsoCheckResponseHeadersTest() {
        Response response = client.target(authnRequestSteps.getStubIdpUri(Urls.LOGIN_RESOURCE))
                .request()
                .cookie(CookieNames.SESSION_COOKIE_NAME, "notAValidRequestId")
                .get();
        assertThat(response.getStatus()).isEqualTo(500);
        final String body = response.readEntity(String.class);
        assertThat(body).contains("Sorry, something went wrong");
        // ensure data not stored by browser
        assertThat(response.getHeaderString(CACHE_CONTROL_KEY)).isEqualTo(CACHE_CONTROL_NO_CACHE_VALUE);
        assertThat(response.getHeaderString(PRAGMA_KEY)).isEqualTo(PRAGMA_NO_CACHE_VALUE);
    }

    @Test
    public void ensureImagesAreCacheableTest() {
        Response response = client.target(authnRequestSteps.getStubIdpUri("/assets/images/providers/stub-idp-one.png"))
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        // ensure data can be stored by browser
        assertThat(response.getHeaderString(CACHE_CONTROL_KEY)).isNull();
        assertThat(response.getHeaderString(PRAGMA_KEY)).isNull();
    }

    @Test
    public void fileNotFoundTest() {
        Response response = client.target(authnRequestSteps.getStubIdpUri("/pathThatDoesNotExist"))
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(404);
        final String body = response.readEntity(String.class);
        assertThat(body).isEmpty();
    }

    @Test
    public void randomizedPidTest() throws IOException, ResolverException, CertificateException {
        final AuthnRequestSteps.Cookies cookies1 = authnRequestSteps.userPostsAuthnRequestToStubIdp();
        authnRequestSteps.userLogsIn(cookies1);
        final String samlResponse = authnRequestSteps.userConsentsReturnSamlResponse(cookies1, false);
        final InboundResponseFromIdp inboundResponseFromIdp = samlDecrypter.decryptSaml(samlResponse);
        final String firstPid = inboundResponseFromIdp.getAuthnStatementAssertion().get().getPersistentId().getNameId();

        final AuthnRequestSteps.Cookies cookies2 = authnRequestSteps.userPostsAuthnRequestToStubIdp();
        authnRequestSteps.userLogsIn(cookies2);
        final String samlResponse2 = authnRequestSteps.userConsentsReturnSamlResponse(cookies2, true);
        final InboundResponseFromIdp inboundResponseFromIdp2 = samlDecrypter.decryptSaml(samlResponse2);
        assertThat(inboundResponseFromIdp2.getAuthnStatementAssertion().get().getPersistentId().getNameId()).isNotEqualTo(firstPid);
    }

}

package uk.gov.ida.apprule;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.apprule.steps.AuthnRequestSteps;
import uk.gov.ida.apprule.support.StubIdpAppRule;
import uk.gov.ida.stub.idp.repositories.StubCountryRepository;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;

import static uk.gov.ida.stub.idp.builders.StubIdpBuilder.aStubIdp;

public class EidasUserLogsInIntegrationTests {

    public static final String DISPLAY_NAME = "User Repository Identity Service";
    private final Client client = JerseyClientBuilder.createClient().property(ClientProperties.FOLLOW_REDIRECTS, false);

    private final AuthnRequestSteps authnRequestSteps = new AuthnRequestSteps(
            client,
            StubCountryRepository.STUB_COUNTRY_FRIENDLY_ID,
            applicationRule.getLocalPort());

    @ClassRule
    public static final StubIdpAppRule applicationRule = new StubIdpAppRule();

    @Before
    public void refreshMetadata() {
        client.target("http://localhost:"+applicationRule.getAdminPort()+"/tasks/connector-metadata-refresh").request().post(Entity.text(""));
    }

    @Test
    public void loginBehaviourTest() {
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsEidasAuthnRequestToStubIdp();
        authnRequestSteps.eidasUserLogsIn(cookies);
        authnRequestSteps.eidasUserConsentsReturnSamlResponse(cookies, false);
    }

    @Test
    public void debugPageLoadsTest() {
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsEidasAuthnRequestToStubIdp();
        authnRequestSteps.eidasUserLogsIn(cookies);
        authnRequestSteps.eidasUserViewsTheDebugPage(cookies);
    }

}

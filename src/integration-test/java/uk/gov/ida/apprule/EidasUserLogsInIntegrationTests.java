package uk.gov.ida.apprule;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.apprule.steps.AuthnRequestSteps;
import uk.gov.ida.apprule.support.StubIdpAppRule;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;

import static uk.gov.ida.stub.idp.builders.StubIdpBuilder.aStubIdp;

public class EidasUserLogsInIntegrationTests {

    private static final String COUNTRY_NAME = "country1";
    public static final String DISPLAY_NAME = "User Repository Identity Service";
    private final Client client = JerseyClientBuilder.createClient().property(ClientProperties.FOLLOW_REDIRECTS, false);

    private final AuthnRequestSteps authnRequestSteps = new AuthnRequestSteps(
            client,
            COUNTRY_NAME,
            applicationRule.getLocalPort());

    @ClassRule
    public static final StubIdpAppRule applicationRule = new StubIdpAppRule()
            .withStubIdp(aStubIdp()
                    .withId(COUNTRY_NAME)
                    .withDisplayName(DISPLAY_NAME)
                    .build());

    @Before
    public void refreshMetadata() {
        client.target("http://localhost:"+applicationRule.getAdminPort()+"/tasks/metadata-refresh").request().post(Entity.text(""));
    }

    @Test
    public void loginBehaviourTest() {
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsEidasAuthnRequestToStubIdp();
        authnRequestSteps.eidasUserLogsIn(cookies);
        authnRequestSteps.eidasUserConsentsReturnSamlResponse(cookies, false);
    }
}

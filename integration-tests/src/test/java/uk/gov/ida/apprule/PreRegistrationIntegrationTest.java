package uk.gov.ida.apprule;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.apprule.steps.FormBuilder;
import uk.gov.ida.apprule.steps.PreRegistrationSteps;
import uk.gov.ida.apprule.support.IntegrationTestHelper;
import uk.gov.ida.apprule.support.StubIdpAppRule;
import uk.gov.ida.apprule.support.TestSamlRequestFactory;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.domain.SubmitButtonValue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static uk.gov.ida.stub.idp.builders.StubIdpBuilder.aStubIdp;
import static uk.gov.ida.stub.idp.csrf.CSRFCheckProtectionFilter.CSRF_PROTECT_FORM_KEY;

public class PreRegistrationIntegrationTest extends IntegrationTestHelper {
    private static final String IDP_NAME = "stub-idp-demo-one";
    private static final String DISPLAY_NAME = "Stub Idp One Pre-Register";
    private static final String FIRSTNAME_PARAM = "Jack";
    private static final String SURNAME_PARAM = "Bauer";
    private static final String ADDRESS_LINE1_PARAM = "123 Letsbe Avenue";
    private static final String ADDRESS_LINE2_PARAM = "Somewhere";
    private static final String ADDRESS_TOWN_PARAM = "Smallville";
    private static final String ADDRESS_POST_CODE_PARAM = "VE7 1FY";
    private static final String DATE_OF_BIRTH_PARAM = "1981-06-06";
    private static final String USERNAME_PARAM = "pre-registering-user";
    private static final String PASSWORD_PARAM = "bar";
    private static final String LEVEL_OF_ASSURANCE_PARAM = AuthnContext.LEVEL_2.name();

    @ClassRule
    public static final StubIdpAppRule applicationRule = new StubIdpAppRule()
            .withStubIdp(aStubIdp().withId(IDP_NAME).withDisplayName(DISPLAY_NAME).build());

    public static Client client = JerseyClientBuilder.createClient().property(ClientProperties.FOLLOW_REDIRECTS, false);

    @Before
    public void setUp() {
        client.target("http://localhost:" + applicationRule.getAdminPort() + "/tasks/metadata-refresh").request().post(Entity.text(""));
    }

    @Test
    public void userPreRegistersAndThenComesFromRP(){
        PreRegistrationSteps steps = new PreRegistrationSteps(client, applicationRule);

        steps

        .userSuccessfullyNavigatesTo(Urls.PRE_REGISTER_RESOURCE)
        .responseContains("Register with " + DISPLAY_NAME)

        .userSubmitsForm(
            FormBuilder.newForm()
                .withParam(Urls.IDP_ID_PARAM, IDP_NAME)
                .withParam(Urls.FIRSTNAME_PARAM, FIRSTNAME_PARAM)
                .withParam(Urls.SURNAME_PARAM, SURNAME_PARAM)
                .withParam(Urls.ADDRESS_LINE1_PARAM, ADDRESS_LINE1_PARAM)
                .withParam(Urls.ADDRESS_LINE2_PARAM, ADDRESS_LINE2_PARAM)
                .withParam(Urls.ADDRESS_TOWN_PARAM, ADDRESS_TOWN_PARAM)
                .withParam(Urls.ADDRESS_POST_CODE_PARAM, ADDRESS_POST_CODE_PARAM)
                .withParam(Urls.DATE_OF_BIRTH_PARAM, DATE_OF_BIRTH_PARAM)
                .withParam(Urls.USERNAME_PARAM, USERNAME_PARAM)
                .withParam(Urls.PASSWORD_PARAM, PASSWORD_PARAM)
                .withParam(Urls.LEVEL_OF_ASSURANCE_PARAM, LEVEL_OF_ASSURANCE_PARAM)
                .withParam(CSRF_PROTECT_FORM_KEY, steps.getCsrfToken())
                .withParam(Urls.SUBMIT_PARAM, SubmitButtonValue.Register.toString())
                .build(),
                Urls.REGISTER_RESOURCE)
        .userIsRedirectedTo(Urls.SINGLE_IDP_PROMPT_RESOURCE+"?source=pre-reg")
        .theRedirectIsFollowed()
        .theResponseStatusIs(Response.Status.OK)
        .responseContains(FIRSTNAME_PARAM,
                            SURNAME_PARAM,
                            ADDRESS_LINE1_PARAM,
                            ADDRESS_LINE2_PARAM,
                            ADDRESS_POST_CODE_PARAM,
                            LEVEL_OF_ASSURANCE_PARAM)
        // ... hub ...

        // Simulate Authn Request from hub
        .clientPostsFormData(FormBuilder.newForm()
                                .withParam(Urls.SAML_REQUEST_PARAM, TestSamlRequestFactory.anAuthnRequest())
                                .withParam(Urls.RELAY_STATE_PARAM, "relay-state")
                                .build(),
                        Urls.IDP_SAML2_SSO_RESOURCE)

        .userIsRedirectedTo(Urls.LOGIN_RESOURCE)
        .theRedirectIsFollowed()
        .userIsRedirectedTo(Urls.CONSENT_RESOURCE)
        .theRedirectIsFollowed()
        .theResponseStatusIs(Response.Status.OK)
        .responseContains(FIRSTNAME_PARAM,
                            SURNAME_PARAM,
                            ADDRESS_LINE1_PARAM,
                            ADDRESS_LINE2_PARAM,
                            ADDRESS_POST_CODE_PARAM,
                            LEVEL_OF_ASSURANCE_PARAM);
    }
}

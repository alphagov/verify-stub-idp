package uk.gov.ida.apprule;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.StatusCode;
import uk.gov.ida.apprule.steps.AuthnRequestSteps;
import uk.gov.ida.apprule.support.IntegrationTestHelper;
import uk.gov.ida.apprule.support.SamlDecrypter;
import uk.gov.ida.apprule.support.StubIdpAppRule;
import uk.gov.ida.apprule.support.eidas.InboundResponseFromCountry;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.hub.domain.LevelOfAssurance;
import uk.gov.ida.stub.idp.repositories.StubCountryRepository;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class EidasUserLogsInIntegrationTests extends IntegrationTestHelper {

    private final Client client = JerseyClientBuilder.createClient().property(ClientProperties.FOLLOW_REDIRECTS, false);
    private final AuthnRequestSteps authnRequestSteps = new AuthnRequestSteps(
            client,
            StubCountryRepository.STUB_COUNTRY_FRIENDLY_ID,
            applicationRule.getLocalPort());
    private final SamlDecrypter samlDecrypter = new SamlDecrypter(client, applicationRule.getMetadataPath(), applicationRule.getConfiguration().getHubEntityId(), applicationRule.getLocalPort());

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
    public void debugPageLoadsAndValuesForOptionalAttribuesAreReturnedTest() {
        // this test requests these attributes and checks that they are displayed on the debug page as requested
        // but stub-country can't currently return any values for these attributes
        final boolean requestGender = true;
        final boolean requestAddress = true;
        final AuthnRequestSteps.Cookies cookies = authnRequestSteps.userPostsEidasAuthnRequestToStubIdpWithAttribute(requestAddress, requestGender);
        authnRequestSteps.eidasUserLogsIn(cookies);
        final String page = authnRequestSteps.eidasUserViewsTheDebugPage(cookies);
        // these can be requested but stub-country currently has no users that contain current address or gender
        if (requestAddress) { assertThat(page).contains(IdaConstants.Eidas_Attributes.CurrentAddress.NAME); }
        if (requestGender) { assertThat(page).contains(IdaConstants.Eidas_Attributes.Gender.NAME); }
        final String samlResponse = authnRequestSteps.eidasUserConsentsReturnSamlResponse(cookies, false);
        final InboundResponseFromCountry inboundResponseFromCountry = samlDecrypter.decryptEidasSaml(samlResponse);
        assertThat(inboundResponseFromCountry.getIssuer()).isEqualTo("http://localhost:0/stub-country/ServiceMetadata");
        assertThat(inboundResponseFromCountry.getStatus().getStatusCode().getValue()).isEqualTo(StatusCode.SUCCESS);
        assertThat(inboundResponseFromCountry.getValidatedIdentityAssertion().getAuthnStatements().size()).isEqualTo(1);
        assertThat(LevelOfAssurance.fromString(inboundResponseFromCountry.getValidatedIdentityAssertion().getAuthnStatements().get(0).getAuthnContext().getAuthnContextClassRef().getAuthnContextClassRef())).isEqualTo(LevelOfAssurance.SUBSTANTIAL);
        assertThat(inboundResponseFromCountry.getValidatedIdentityAssertion().getAttributeStatements().size()).isEqualTo(1);
        final List<Attribute> attributes = inboundResponseFromCountry.getValidatedIdentityAssertion().getAttributeStatements().get(0).getAttributes();
        // stub-country can currently only return these 4 attributes
        assertThat(attributes.size()).isEqualTo(4);
        assertThat(attributes.stream().map(a -> a.getName()).collect(Collectors.toList()))
                .containsExactlyInAnyOrder(
                        IdaConstants.Eidas_Attributes.FirstName.NAME,
                        IdaConstants.Eidas_Attributes.FamilyName.NAME,
                        IdaConstants.Eidas_Attributes.PersonIdentifier.NAME,
                        IdaConstants.Eidas_Attributes.DateOfBirth.NAME);
    }

}

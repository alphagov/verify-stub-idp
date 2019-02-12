package uk.gov.ida.apprule;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.ida.apprule.support.IntegrationTestHelper;
import uk.gov.ida.apprule.support.StubIdpAppRule;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.stub.idp.builders.StubIdpBuilder.aStubIdp;

public class EidasCountryMetadataIntegrationTests extends IntegrationTestHelper {

    private static final String COUNTRY_NAME = "country1";
    public static final String DISPLAY_NAME = "User Repository Identity Service";
    private final Client client = JerseyClientBuilder.createClient().property(ClientProperties.FOLLOW_REDIRECTS, false);

    @ClassRule
    public static final StubIdpAppRule applicationRule = new StubIdpAppRule()
            .withStubIdp(aStubIdp()
                    .withId(COUNTRY_NAME)
                    .withDisplayName(DISPLAY_NAME)
                    .build());

    @Before
    public void refreshMetadata() {
        client.target("http://localhost:"+applicationRule.getAdminPort()+"/tasks/connector-metadata-refresh").request().post(Entity.text(""));
    }

    @Test
    public void countryMetadataShouldContainCorrectEntityIdAndSsoUrl() {
        String baseUrl = applicationRule.getConfiguration().getEuropeanIdentityConfiguration().getStubCountryBaseUrl();
        String metadataEndpoint = baseUrl + "/stub-country/ServiceMetadata";
        String expectedSsoUrl = baseUrl + "/eidas/stub-country/SAML2/SSO";

        Response response = client.target("http://localhost:"+applicationRule.getPort(0) + "/stub-country/ServiceMetadata").request().get();
        Document metadata = response.readEntity(Document.class);

        String entityId =  metadata.getDocumentElement().getAttributes().getNamedItem("entityID").getNodeValue();
        String ssoUrl =  getChildByTagName(getChildByTagName(metadata.getDocumentElement(), "md:IDPSSODescriptor"), "md:SingleSignOnService").getAttributes().getNamedItem("Location").getNodeValue();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(entityId).isEqualTo(metadataEndpoint);
        assertThat(ssoUrl).isEqualTo(expectedSsoUrl);
    }

    private Node getChildByTagName(Node element, String tagName) {
        NodeList children = element.getChildNodes();
        for(int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeName().equals(tagName)) {
                return children.item(i);
            }
        }
        return null;
    }
}

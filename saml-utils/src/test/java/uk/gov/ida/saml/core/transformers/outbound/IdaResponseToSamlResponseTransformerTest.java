package uk.gov.ida.saml.core.transformers.outbound;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.domain.OutboundResponseFromHub;
import uk.gov.ida.saml.core.test.OpenSAMLMockitoRunner;
import uk.gov.ida.saml.core.test.TestEntityIds;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.core.test.builders.ResponseForHubBuilder.anAuthnResponse;

@RunWith(OpenSAMLMockitoRunner.class)
public class IdaResponseToSamlResponseTransformerTest {

    private IdaResponseToSamlResponseTransformer<OutboundResponseFromHub> systemUnderTest;

    @Before
    public void setup() {
        OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
        systemUnderTest = new TestTransformer(openSamlXmlObjectFactory);
    }

    @Test
    public void transform_shouldTransformResponseId() throws Exception {
    	OutboundResponseFromHub idaResponse = anAuthnResponse().withResponseId("response-id").buildOutboundResponseFromHub();

        Response transformedResponse = systemUnderTest.apply(idaResponse);

        assertThat(transformedResponse.getID()).isEqualTo(idaResponse.getId());
    }

    @Test
    public void transform_shouldTransformIssueInstant() throws Exception {
        DateTime issueInstant = new DateTime(2012, 1, 2, 3, 4);
        OutboundResponseFromHub idaResponse = anAuthnResponse().withIssueInstant(issueInstant).buildOutboundResponseFromHub();
        Response transformedResponse = systemUnderTest.apply(idaResponse);

        assertThat(transformedResponse.getIssueInstant().isEqual(idaResponse.getIssueInstant())).isTrue();
    }

    @Test
    public void transform_shouldTransformInResponseToIfPresent() throws Exception {
        String inResponseTo = "id of original request";
        OutboundResponseFromHub idaResponse = anAuthnResponse().withInResponseTo(inResponseTo).buildOutboundResponseFromHub();
        Response transformedResponse = systemUnderTest.apply(idaResponse);

        assertThat(transformedResponse.getInResponseTo()).isEqualTo(inResponseTo);
    }

    @Test
    public void transform_shouldNotTransformInResponseToIfMissing() throws Exception {
    	OutboundResponseFromHub idaResponse = anAuthnResponse().withInResponseTo(null).buildOutboundResponseFromHub();
        Response transformedResponse = systemUnderTest.apply(idaResponse);

        assertThat(transformedResponse.getInResponseTo()).isNull();
    }

    @Test
    public void transform_shouldTransformIssuer() throws Exception {
        String issuer = "response issuer";
        OutboundResponseFromHub idaResponse = anAuthnResponse().withIssuerId(issuer).buildOutboundResponseFromHub();
        Response transformedResponse = systemUnderTest.apply(idaResponse);

        assertThat(transformedResponse.getIssuer().getValue()).isEqualTo(TestEntityIds.HUB_ENTITY_ID);
    }

    public static class TestTransformer extends IdaResponseToSamlResponseTransformer<OutboundResponseFromHub>{

        public TestTransformer(
                OpenSamlXmlObjectFactory openSamlXmlObjectFactory) {
            super(openSamlXmlObjectFactory);
        }

        @Override
        protected void transformAssertions(OutboundResponseFromHub originalResponse, Response transformedResponse) {
        }

        @Override
        protected Status transformStatus(OutboundResponseFromHub originalResponse) {
            return null;
        }

        @Override
        protected void transformDestination(OutboundResponseFromHub originalResponse, Response transformedResponse) {
        }
    }
}

package uk.gov.ida.stub.idp.builders;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Response;
import uk.gov.ida.saml.core.IdaSamlBootstrap;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EidasResponseBuilderTest {

    @BeforeClass
    public static void setUpClass() {
        IdaSamlBootstrap.bootstrap();
    }

    @Before
    public void setUp() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }

    @After
    public void teardown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldCreateEidasResponseWithRequiredFields() throws Exception {
        EidasResponseBuilder eidasResponseBuilder = new EidasResponseBuilder("connectorNodeIssuerId");
        List<Attribute> attributes = Collections.emptyList();
        DateTime issueInstant = DateTime.now().minusSeconds(2);
        DateTime assertionIssueInstant = DateTime.now().minusSeconds(1);
        DateTime authnStatementIssueInstant = DateTime.now();

        Response response = eidasResponseBuilder.createEidasResponse("responseIssuerId", "statusCodeValue",
                "pid", "loa", attributes, "inResponseTo", issueInstant, assertionIssueInstant, authnStatementIssueInstant, "destinationUrl");
        Assertion assertion = response.getAssertions().get(0);
        AuthnStatement authnStatement = assertion.getAuthnStatements().get(0);

        assertThat(assertion.getAttributeStatements().get(0).getAttributes()).isEqualTo(attributes);
        assertThat(authnStatement.getAuthnContext().getAuthnContextClassRef().getAuthnContextClassRef()).isEqualTo("loa");
        assertThat(authnStatement.getAuthnInstant().getMillis()).isEqualTo(authnStatementIssueInstant.getMillis());
        assertThat(assertion.getSubject().getNameID().getValue()).isEqualTo("UK/EU/pid");
        assertThat(assertion.getSubject().getNameID().getFormat()).isEqualTo(NameIDType.PERSISTENT);
        assertThat(assertion.getIssuer().getValue()).isEqualTo("responseIssuerId");
        assertThat(assertion.getConditions().getNotBefore().getMillis()).isEqualTo(DateTime.now().getMillis());
        assertThat(assertion.getConditions().getNotOnOrAfter().getMillis()).isEqualTo(DateTime.now().plusMinutes(5).getMillis());
        assertThat(assertion.getConditions().getAudienceRestrictions().get(0).getAudiences().get(0).getAudienceURI()).isEqualTo("connectorNodeIssuerId");
        assertThat(response.getIssuer().getValue()).isEqualTo("responseIssuerId");
        assertThat(response.getID()).isNotBlank();
        assertThat(response.getInResponseTo()).isEqualTo("inResponseTo");
        assertThat(response.getDestination()).isEqualTo("destinationUrl");
        assertThat(response.getIssueInstant().getMillis()).isEqualTo(issueInstant.getMillis());
        assertThat(response.getStatus().getStatusCode().getValue()).isEqualTo("statusCodeValue");
    }
}
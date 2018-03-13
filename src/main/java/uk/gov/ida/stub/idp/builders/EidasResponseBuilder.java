package uk.gov.ida.stub.idp.builders;

import net.shibboleth.utilities.java.support.security.SecureRandomIdentifierGenerationStrategy;
import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;

import javax.xml.namespace.QName;
import java.util.List;

public class EidasResponseBuilder {

    private static final SecureRandomIdentifierGenerationStrategy idGeneratorStrategy = new SecureRandomIdentifierGenerationStrategy();
    private Response eidasResponse;

    public EidasResponseBuilder() {
        eidasResponse = build(Response.DEFAULT_ELEMENT_NAME);
    }

    public EidasResponseBuilder withId(String id) {
        eidasResponse.setID(id);
        return this;
    }

    public EidasResponseBuilder withIssuer(String issuerId) {
        eidasResponse.setIssuer(createIssuer(issuerId));
        return this;
    }

    public EidasResponseBuilder withStatus(String statusCodeValue) {
        eidasResponse.setStatus(createStatus(statusCodeValue));
        return this;
    }

    public EidasResponseBuilder withInResponseTo(String inResponseTo) {
        eidasResponse.setInResponseTo(inResponseTo);
        return this;
    }

    public EidasResponseBuilder withDestination(String destination) {
        eidasResponse.setDestination(destination);
        return this;
    }

    public EidasResponseBuilder withIssueInstant(DateTime issueInstant) {
        eidasResponse.setIssueInstant(issueInstant);
        return this;
    }

    public EidasResponseBuilder addAssertion(Assertion assertion) {
        eidasResponse.getAssertions().add(assertion);
        return this;
    }
    public Response build() {
        return eidasResponse;
    }

    public static Response createEidasResponse(
        String responseIssuerId,
        String statusCodeValue,
        String pid,
        String loa,
        List<Attribute> attributes,
        String inResponseTo,
        DateTime issueInstant,
        DateTime assertionIssueInstant,
        DateTime authnStatementAuthnInstant,
        String destinationUrl,
        String connectorNodeIssuerId
    ) {
        Assertion assertion = new EidasAssertionBuilder()
            .withId(generateRandomId())
            .withSubject(pid, inResponseTo, destinationUrl)
            .withIssuer(responseIssuerId)
            .withIssueInstant(assertionIssueInstant)
            .withConditions(connectorNodeIssuerId)
            .addAuthnStatement(loa, authnStatementAuthnInstant)
            .addAttributeStatement(attributes)
            .build();

        return new EidasResponseBuilder()
                .withId(generateRandomId())
                .withIssuer(responseIssuerId)
                .withStatus(statusCodeValue)
                .withInResponseTo(inResponseTo)
                .withDestination(destinationUrl)
                .withIssueInstant(issueInstant)
                .addAssertion(assertion)
                .build();
    }

    private static String generateRandomId(){
        return idGeneratorStrategy.generateIdentifier(true);
    }

    private Status createStatus(String statusCodeValue) {
        Status status = build(Status.DEFAULT_ELEMENT_NAME);
        StatusCode statusCode = build(StatusCode.DEFAULT_ELEMENT_NAME);
        statusCode.setValue(statusCodeValue);
        status.setStatusCode(statusCode);
        return status;
    }

    private Issuer createIssuer(String issuerId) {
        Issuer issuer = build(Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setFormat(NameIDType.ENTITY);
        issuer.setValue(issuerId);
        return issuer;
    }

    private static <T extends XMLObject> T build(QName elementName) {
        return (T) XMLObjectSupport.buildXMLObject(elementName);
    }
}

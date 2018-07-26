package uk.gov.ida.stub.idp.builders;

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.saml2.core.*;

import javax.xml.namespace.QName;
import java.util.List;

public class EidasAssertionBuilder {

    public static final String TEMPORARY_PID_TRANSLATION = "UK/EU/";

    private final Assertion assertion;

    public EidasAssertionBuilder() {
        assertion = build(Assertion.DEFAULT_ELEMENT_NAME);
    }

    public EidasAssertionBuilder withId(String id) {
        assertion.setID(id);
        return this;
    }

    public EidasAssertionBuilder withSubject(String pid, String inResponseTo, String destinationUrl) {
        assertion.setSubject(createSubject(pid, inResponseTo, destinationUrl));
        return this;
    }

    public EidasAssertionBuilder withIssuer(String issuerId) {
        assertion.setIssuer(createIssuer(issuerId));
        return this;
    }

    public EidasAssertionBuilder withIssueInstant(DateTime issueInstant) {
        assertion.setIssueInstant(issueInstant);
        return this;
    }

    public EidasAssertionBuilder withConditions(String audienceUri) {
        assertion.setConditions(createConditions(audienceUri));
        return this;
    }
    public EidasAssertionBuilder addAuthnStatement(String loa, DateTime authnIssueInstant) {
        assertion.getAuthnStatements().add(createAuthnStatement(loa, authnIssueInstant));
        return this;
    }

    public EidasAssertionBuilder addAttributeStatement(List<Attribute> attributes) {
        assertion.getAttributeStatements().add(createAttributeStatement(attributes));
        return this;
    }

    public Assertion build() {
        return assertion;
    }

    private Subject createSubject(String pid, String inResponseTo, String destinationUrl) {
        Subject subject = build(Subject.DEFAULT_ELEMENT_NAME);
        NameID nameID = build(NameID.DEFAULT_ELEMENT_NAME);
        nameID.setValue(TEMPORARY_PID_TRANSLATION + pid);
        nameID.setFormat(NameIDType.PERSISTENT);
        subject.setNameID(nameID);
        SubjectConfirmation confirmation = createSubjectConfirmation(inResponseTo, destinationUrl);
        subject.getSubjectConfirmations().add(confirmation);
        return subject;
    }

    private SubjectConfirmation createSubjectConfirmation(String inResponseTo, String destinationUrl) {
        SubjectConfirmationData subjectConfirmationData = build(SubjectConfirmationData.DEFAULT_ELEMENT_NAME);
        subjectConfirmationData.setInResponseTo(inResponseTo);
        subjectConfirmationData.setNotOnOrAfter(DateTime.now().plusMinutes(5));
        subjectConfirmationData.setRecipient(destinationUrl);

        SubjectConfirmation subjectConfirmation = build(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
        subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);

        return subjectConfirmation;
    }

    private Conditions createConditions(String audienceUri) {
        Audience audience = build(Audience.DEFAULT_ELEMENT_NAME);
        audience.setAudienceURI(audienceUri);

        AudienceRestriction audienceRestriction = build(AudienceRestriction.DEFAULT_ELEMENT_NAME);
        audienceRestriction.getAudiences().add(audience);

        Conditions conditions = build(Conditions.DEFAULT_ELEMENT_NAME);
        DateTime now = DateTime.now();
        conditions.setNotBefore(now);
        conditions.setNotOnOrAfter(now.plusMinutes(5));
        conditions.getAudienceRestrictions().add(audienceRestriction);
        return conditions;
    }

    private AttributeStatement createAttributeStatement(List<Attribute> attributes) {
        AttributeStatement attributeStatement = build(AttributeStatement.DEFAULT_ELEMENT_NAME);
        attributeStatement.getAttributes().addAll(attributes);
        return attributeStatement;
    }

    private AuthnStatement createAuthnStatement(String loa, DateTime authnStatementAuthnInstant) {
        AuthnStatement authnStatement = build(AuthnStatement.DEFAULT_ELEMENT_NAME);
        AuthnContext authnContext = build(AuthnContext.DEFAULT_ELEMENT_NAME);
        AuthnContextClassRef authnContextClassRef = build(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        authnContextClassRef.setAuthnContextClassRef(loa);
        authnContext.setAuthnContextClassRef(authnContextClassRef);
        authnStatement.setAuthnContext(authnContext);
        authnStatement.setAuthnInstant(authnStatementAuthnInstant);
        return authnStatement;
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

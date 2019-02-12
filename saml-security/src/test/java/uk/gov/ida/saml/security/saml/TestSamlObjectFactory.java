package uk.gov.ida.saml.security.saml;

import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeQuery;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import uk.gov.ida.saml.core.extensions.IPAddress;

public class TestSamlObjectFactory {

    private XMLObjectBuilderFactory openSamlBuilderFactory;

    public TestSamlObjectFactory() {
        openSamlBuilderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
    }

    public Subject createSubject() {
        return (Subject) openSamlBuilderFactory.getBuilder(Subject.DEFAULT_ELEMENT_NAME).buildObject(Subject.DEFAULT_ELEMENT_NAME, Subject.TYPE_NAME);
    }

    public AttributeQuery createAttributeQuery() {
        return (AttributeQuery) openSamlBuilderFactory.getBuilder(AttributeQuery.DEFAULT_ELEMENT_NAME).buildObject(AttributeQuery.DEFAULT_ELEMENT_NAME, AttributeQuery.TYPE_NAME);
    }

    public Issuer createIssuer(String issuer) {
        Issuer retVal = (Issuer) openSamlBuilderFactory.getBuilder(Issuer.DEFAULT_ELEMENT_NAME).buildObject(Issuer.DEFAULT_ELEMENT_NAME);
        retVal.setFormat(Issuer.ENTITY);
        retVal.setValue(issuer);
        return retVal;
    }

    public Status createStatus() {
        return (Status) openSamlBuilderFactory.getBuilder(Status.DEFAULT_ELEMENT_NAME).buildObject(Status.DEFAULT_ELEMENT_NAME, Status.TYPE_NAME);
    }

    public StatusCode createStatusCode() {
        return (StatusCode) openSamlBuilderFactory.getBuilder(StatusCode.DEFAULT_ELEMENT_NAME).buildObject(StatusCode.DEFAULT_ELEMENT_NAME, StatusCode.TYPE_NAME);
    }

    public Attribute createAttribute() {
        return (Attribute) openSamlBuilderFactory.getBuilder(Attribute.DEFAULT_ELEMENT_NAME).buildObject(Attribute.DEFAULT_ELEMENT_NAME, Attribute.TYPE_NAME);
    }

    public AttributeStatement createAttributeStatement() {
        return (AttributeStatement) openSamlBuilderFactory.getBuilder(AttributeStatement.DEFAULT_ELEMENT_NAME).buildObject(AttributeStatement.DEFAULT_ELEMENT_NAME, AttributeStatement.TYPE_NAME);
    }

    public Response createResponse() {
        return (Response) openSamlBuilderFactory.getBuilder(Response.DEFAULT_ELEMENT_NAME).buildObject(Response.DEFAULT_ELEMENT_NAME, Response.TYPE_NAME);
    }

    public Assertion createAssertion() {
        return (Assertion) openSamlBuilderFactory.getBuilder(Assertion.DEFAULT_ELEMENT_NAME).buildObject(Assertion.DEFAULT_ELEMENT_NAME, Assertion.TYPE_NAME);
    }

    public SubjectConfirmation createSubjectConfirmation() {
        return (SubjectConfirmation) openSamlBuilderFactory.getBuilder(SubjectConfirmation.DEFAULT_ELEMENT_NAME).buildObject(SubjectConfirmation.DEFAULT_ELEMENT_NAME, SubjectConfirmation.TYPE_NAME);
    }

    public SubjectConfirmationData createSubjectConfirmationData() {
        return (SubjectConfirmationData) openSamlBuilderFactory.getBuilder(SubjectConfirmationData.DEFAULT_ELEMENT_NAME).buildObject(SubjectConfirmationData.DEFAULT_ELEMENT_NAME, SubjectConfirmationData.TYPE_NAME);
    }

    public AuthnRequest createAuthnRequest() {
        return (AuthnRequest) openSamlBuilderFactory.getBuilder(AuthnRequest.DEFAULT_ELEMENT_NAME).buildObject(AuthnRequest.DEFAULT_ELEMENT_NAME, AuthnRequest.TYPE_NAME);
    }

    public Conditions createConditions() {
        return (Conditions) openSamlBuilderFactory.getBuilder(Conditions.DEFAULT_ELEMENT_NAME).buildObject(Conditions.DEFAULT_ELEMENT_NAME, Conditions.TYPE_NAME);
    }

    public RequestedAuthnContext createRequestedAuthnContext(AuthnContextComparisonTypeEnumeration authnContextComparisonTypeEnumeration) {
        RequestedAuthnContext requestedAuthnContext = (RequestedAuthnContext) openSamlBuilderFactory.getBuilder(RequestedAuthnContext.DEFAULT_ELEMENT_NAME).buildObject(RequestedAuthnContext.DEFAULT_ELEMENT_NAME);
        requestedAuthnContext.setComparison(authnContextComparisonTypeEnumeration);
        return requestedAuthnContext;
    }

    public AuthnContext createAuthnContext() {
        return (AuthnContext) openSamlBuilderFactory.getBuilder(AuthnContext.DEFAULT_ELEMENT_NAME).buildObject(AuthnContext.DEFAULT_ELEMENT_NAME);
    }

    public AuthnContextClassRef createAuthnContextClassReference(String authnContextUrn) {
        AuthnContextClassRef authnContextClassRef = (AuthnContextClassRef) openSamlBuilderFactory.getBuilder(AuthnContextClassRef.DEFAULT_ELEMENT_NAME).buildObject(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        authnContextClassRef.setAuthnContextClassRef(authnContextUrn);
        return authnContextClassRef;
    }

    public AuthnStatement createAuthnStatement() {
        return (AuthnStatement) openSamlBuilderFactory.getBuilder(AuthnStatement.TYPE_NAME).buildObject(AuthnStatement.DEFAULT_ELEMENT_NAME, AuthnStatement.TYPE_NAME);
    }

    public SAMLVersion createSamlVersion(String samlVersion) {
        return SAMLVersion.valueOf(samlVersion);
    }

    public IPAddress createIPAddressAttributeValue(String value) {
        IPAddress ipAddressObject = (IPAddress) openSamlBuilderFactory.getBuilder(IPAddress.TYPE_NAME).buildObject(IPAddress.DEFAULT_ELEMENT_NAME, IPAddress.TYPE_NAME);
        ipAddressObject.setValue(value);
        return ipAddressObject;
    }

    public NameID createNameId(String nameId) {
        NameID retVal = (NameID) openSamlBuilderFactory.getBuilder(NameID.DEFAULT_ELEMENT_NAME).buildObject(NameID.DEFAULT_ELEMENT_NAME);
        retVal.setFormat(NameID.PERSISTENT);
        retVal.setValue(nameId);
        return retVal;
    }

    public AudienceRestriction createAudienceRestriction(String audienceId) {
        Audience audience = createAudience(audienceId);
        AudienceRestriction audienceRestriction = (AudienceRestriction) openSamlBuilderFactory.getBuilder(AudienceRestriction.DEFAULT_ELEMENT_NAME).buildObject(AudienceRestriction.DEFAULT_ELEMENT_NAME, AudienceRestriction.TYPE_NAME);
        audienceRestriction.getAudiences().add(audience);

        return audienceRestriction;
    }

    private Audience createAudience(String audienceId) {
        Audience audience = (Audience) openSamlBuilderFactory.getBuilder(Audience.DEFAULT_ELEMENT_NAME).buildObject(Audience.DEFAULT_ELEMENT_NAME);
        audience.setAudienceURI(audienceId);

        return audience;
    }

}

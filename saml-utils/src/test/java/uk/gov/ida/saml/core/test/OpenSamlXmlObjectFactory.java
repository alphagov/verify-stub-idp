package uk.gov.ida.saml.core.test;

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
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.extensions.Address;
import uk.gov.ida.saml.core.extensions.Date;
import uk.gov.ida.saml.core.extensions.Gender;
import uk.gov.ida.saml.core.extensions.Gpg45Status;
import uk.gov.ida.saml.core.extensions.IPAddress;
import uk.gov.ida.saml.core.extensions.InternationalPostCode;
import uk.gov.ida.saml.core.extensions.Line;
import uk.gov.ida.saml.core.extensions.PersonName;
import uk.gov.ida.saml.core.extensions.PostCode;
import uk.gov.ida.saml.core.extensions.StringBasedMdsAttributeValue;
import uk.gov.ida.saml.core.extensions.UPRN;
import uk.gov.ida.saml.core.extensions.eidas.PersonIdentifier;

/**
 * OpenSamlXmlObjectFactory wraps the underlying Open-Saml XMLObjectBuilderFactory
 *
 * Where possible use XMLObjectBuilderFactory. If you need to hide some of the
 * complexity please create a class in the project you're using.
 *
 * @deprecated
 */
public class OpenSamlXmlObjectFactory {

    private XMLObjectBuilderFactory openSamlBuilderFactory;

    public OpenSamlXmlObjectFactory() {
        openSamlBuilderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
    }

    public Subject createSubject() {
        return (Subject) openSamlBuilderFactory.getBuilder(Subject.DEFAULT_ELEMENT_NAME).buildObject(Subject.DEFAULT_ELEMENT_NAME, Subject.TYPE_NAME);
    }

    public AttributeQuery createAttributeQuery() {
        return (AttributeQuery) openSamlBuilderFactory.getBuilder(AttributeQuery.DEFAULT_ELEMENT_NAME).buildObject(AttributeQuery.DEFAULT_ELEMENT_NAME, AttributeQuery.TYPE_NAME);
    }

    public NameID createNameId(String nameId) {
        NameID retVal = (NameID) openSamlBuilderFactory.getBuilder(NameID.DEFAULT_ELEMENT_NAME).buildObject(NameID.DEFAULT_ELEMENT_NAME);
        retVal.setFormat(NameID.PERSISTENT);
        retVal.setValue(nameId);
        return retVal;
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

    private Audience createAudience(String audienceId) {
        Audience audience = (Audience) openSamlBuilderFactory.getBuilder(Audience.DEFAULT_ELEMENT_NAME).buildObject(Audience.DEFAULT_ELEMENT_NAME);
        audience.setAudienceURI(audienceId);

        return audience;
    }

    public AudienceRestriction createAudienceRestriction(String audienceId) {
        Audience audience = createAudience(audienceId);
        AudienceRestriction audienceRestriction = (AudienceRestriction) openSamlBuilderFactory.getBuilder(AudienceRestriction.DEFAULT_ELEMENT_NAME).buildObject(AudienceRestriction.DEFAULT_ELEMENT_NAME, AudienceRestriction.TYPE_NAME);
        audienceRestriction.getAudiences().add(audience);

        return audienceRestriction;
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

    public Address createAddressAttributeValue() {
        return (Address) openSamlBuilderFactory.getBuilder(Address.TYPE_NAME).buildObject(Address.DEFAULT_ELEMENT_NAME, Address.TYPE_NAME);
    }

    public PostCode createPostCode(String postCode) {
        PostCode postCodeObject = (PostCode) openSamlBuilderFactory.getBuilder(PostCode.DEFAULT_ELEMENT_NAME).buildObject(PostCode.DEFAULT_ELEMENT_NAME);
        postCodeObject.setValue(postCode);
        return postCodeObject;
    }

    public InternationalPostCode createInternationalPostCode(String internationalPostCode) {
        InternationalPostCode internationalPostCodeObject = (InternationalPostCode) openSamlBuilderFactory.getBuilder(InternationalPostCode.DEFAULT_ELEMENT_NAME).buildObject(InternationalPostCode.DEFAULT_ELEMENT_NAME);
        internationalPostCodeObject.setValue(internationalPostCode);
        return internationalPostCodeObject;
    }

    public UPRN createUPRN(String uprn) {
        UPRN uprnObject = (UPRN) openSamlBuilderFactory.getBuilder(UPRN.DEFAULT_ELEMENT_NAME).buildObject(UPRN.DEFAULT_ELEMENT_NAME);
        uprnObject.setValue(uprn);
        return uprnObject;
    }

    public Line createLine(String line) {
        Line lineObject = (Line) openSamlBuilderFactory.getBuilder(Line.DEFAULT_ELEMENT_NAME).buildObject(Line.DEFAULT_ELEMENT_NAME);
        lineObject.setValue(line);
        return lineObject;
    }

    public PersonName createPersonNameAttributeValue(String name) {
        PersonName personNameObject = (PersonName) openSamlBuilderFactory.getBuilder(PersonName.TYPE_NAME).buildObject(PersonName.DEFAULT_ELEMENT_NAME, PersonName.TYPE_NAME);
        personNameObject.setValue(name);
        personNameObject.setLanguage(IdaConstants.IDA_LANGUAGE);
        return personNameObject;
    }

    public PersonIdentifier createPersonIdentifierAttributeValue(String pid) {
        PersonIdentifier personNameObject = (PersonIdentifier) openSamlBuilderFactory.getBuilder(PersonIdentifier.TYPE_NAME).buildObject(PersonIdentifier.DEFAULT_ELEMENT_NAME, PersonIdentifier.TYPE_NAME);
        personNameObject.setPersonIdentifier(pid);
        return personNameObject;
    }

    public PersonIdentifier createPersonIdentifierAttribute(PersonIdentifier pid) {
        return null;
    }

    public Gender createGenderAttributeValue(String value) {
        Gender genderObject = (Gender) openSamlBuilderFactory.getBuilder(Gender.TYPE_NAME).buildObject(Gender.DEFAULT_ELEMENT_NAME, Gender.TYPE_NAME);
        genderObject.setValue(value);
        return genderObject;
    }

    public Date createDateAttributeValue(String dateTime) {
        Date dateObject = (Date) openSamlBuilderFactory.getBuilder(Date.TYPE_NAME).buildObject(Date.DEFAULT_ELEMENT_NAME, Date.TYPE_NAME);
        dateObject.setValue(dateTime);
        return dateObject;
    }

    public AuthnStatement createAuthnStatement() {
        return (AuthnStatement) openSamlBuilderFactory.getBuilder(AuthnStatement.TYPE_NAME).buildObject(AuthnStatement.DEFAULT_ELEMENT_NAME, AuthnStatement.TYPE_NAME);
    }

    public SAMLVersion createSamlVersion(String samlVersion) {
        return SAMLVersion.valueOf(samlVersion);
    }

    public StringBasedMdsAttributeValue createSimpleMdsAttributeValue(String value) {
        StringBasedMdsAttributeValue stringBasedMdsAttributeValue = (StringBasedMdsAttributeValue) openSamlBuilderFactory.getBuilder(StringBasedMdsAttributeValue.TYPE_NAME).buildObject(StringBasedMdsAttributeValue.DEFAULT_ELEMENT_NAME, StringBasedMdsAttributeValue.TYPE_NAME);
        stringBasedMdsAttributeValue.setValue(value);
        return stringBasedMdsAttributeValue;
    }

    public Gpg45Status createGpg45StatusAttributeValue(String indicator) {
        Gpg45Status gpg45Status = (Gpg45Status) openSamlBuilderFactory.getBuilder(Gpg45Status.TYPE_NAME).buildObject(Gpg45Status.DEFAULT_ELEMENT_NAME, Gpg45Status.TYPE_NAME);
        gpg45Status.setValue(indicator);
        return gpg45Status;
    }

    public IPAddress createIPAddressAttributeValue(String value) {
        IPAddress ipAddressObject = (IPAddress) openSamlBuilderFactory.getBuilder(IPAddress.TYPE_NAME).buildObject(IPAddress.DEFAULT_ELEMENT_NAME, IPAddress.TYPE_NAME);
        ipAddressObject.setValue(value);
        return ipAddressObject;
    }
}

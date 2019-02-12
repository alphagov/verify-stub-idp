package uk.gov.ida.saml.core;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.xml.SAMLConstants;
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
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Scoping;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.StatusDetail;
import org.opensaml.saml.saml2.core.StatusMessage;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.AttributeAuthorityDescriptor;
import org.opensaml.saml.saml2.metadata.AttributeService;
import org.opensaml.saml.saml2.metadata.Company;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.EmailAddress;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.GivenName;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.OrganizationDisplayName;
import org.opensaml.saml.saml2.metadata.OrganizationName;
import org.opensaml.saml.saml2.metadata.OrganizationURL;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml.saml2.metadata.SurName;
import org.opensaml.saml.saml2.metadata.TelephoneNumber;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyName;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import uk.gov.ida.saml.core.extensions.Address;
import uk.gov.ida.saml.core.extensions.Date;
import uk.gov.ida.saml.core.extensions.Gender;
import uk.gov.ida.saml.core.extensions.Gpg45Status;
import uk.gov.ida.saml.core.extensions.IPAddress;
import uk.gov.ida.saml.core.extensions.IdpFraudEventId;
import uk.gov.ida.saml.core.extensions.InternationalPostCode;
import uk.gov.ida.saml.core.extensions.Line;
import uk.gov.ida.saml.core.extensions.PersonName;
import uk.gov.ida.saml.core.extensions.PostCode;
import uk.gov.ida.saml.core.extensions.StatusValue;
import uk.gov.ida.saml.core.extensions.StringBasedMdsAttributeValue;
import uk.gov.ida.saml.core.extensions.UPRN;
import uk.gov.ida.saml.core.extensions.Verified;

import javax.validation.constraints.NotNull;
import javax.xml.namespace.QName;

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

    public StatusMessage createStatusMessage() {
        return (StatusMessage) openSamlBuilderFactory.getBuilder(StatusMessage.DEFAULT_ELEMENT_NAME).buildObject(StatusMessage.DEFAULT_ELEMENT_NAME);
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

    public Scoping createScoping() {
        return (Scoping) openSamlBuilderFactory.getBuilder(Scoping.DEFAULT_ELEMENT_NAME).buildObject(Scoping.DEFAULT_ELEMENT_NAME);
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

    public NameIDPolicy createNameIdPolicy() {
        return (NameIDPolicy) openSamlBuilderFactory.getBuilder(NameIDPolicy.DEFAULT_ELEMENT_NAME).buildObject(NameIDPolicy.DEFAULT_ELEMENT_NAME, NameIDPolicy.TYPE_NAME);
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

    public Verified createVerifiedAttributeValue(boolean value) {
        Verified verifiedObject = (Verified) openSamlBuilderFactory.getBuilder(Verified.TYPE_NAME).buildObject(Verified.DEFAULT_ELEMENT_NAME, Verified.TYPE_NAME);
        verifiedObject.setValue(value);
        return verifiedObject;
    }

    public AuthnStatement createAuthnStatement() {
        return (AuthnStatement) openSamlBuilderFactory.getBuilder(AuthnStatement.TYPE_NAME).buildObject(AuthnStatement.DEFAULT_ELEMENT_NAME, AuthnStatement.TYPE_NAME);
    }

    public EntityDescriptor createEntityDescriptor() {
        return (EntityDescriptor) openSamlBuilderFactory.getBuilder(EntityDescriptor.TYPE_NAME).buildObject(EntityDescriptor.DEFAULT_ELEMENT_NAME, EntityDescriptor.TYPE_NAME);
    }

    public Organization createOrganization() {
        return (Organization) openSamlBuilderFactory.getBuilder(Organization.TYPE_NAME).buildObject(Organization.DEFAULT_ELEMENT_NAME, Organization.TYPE_NAME);
    }

    public SingleSignOnService createSingleSignOnService(String binding, String location) {
        SingleSignOnService singleSignOnService = (SingleSignOnService) openSamlBuilderFactory.getBuilder(SingleSignOnService.DEFAULT_ELEMENT_NAME).buildObject(SingleSignOnService.DEFAULT_ELEMENT_NAME, SingleSignOnService.TYPE_NAME);
        singleSignOnService.setBinding(binding);
        singleSignOnService.setLocation(location);
        return singleSignOnService;
    }

    public AssertionConsumerService createAssertionConsumerService(String binding, String location, Integer index, boolean isDefault) {
        AssertionConsumerService assertionConsumerService = (AssertionConsumerService) openSamlBuilderFactory.getBuilder(AssertionConsumerService.DEFAULT_ELEMENT_NAME).buildObject(AssertionConsumerService.DEFAULT_ELEMENT_NAME, AssertionConsumerService.TYPE_NAME);
        assertionConsumerService.setBinding(binding);
        assertionConsumerService.setLocation(location);
        assertionConsumerService.setIndex(index);
        assertionConsumerService.setIsDefault(isDefault);
        return assertionConsumerService;
    }

    public IDPSSODescriptor createIDPSSODescriptor() {
        return (IDPSSODescriptor) openSamlBuilderFactory.getBuilder(IDPSSODescriptor.DEFAULT_ELEMENT_NAME).buildObject(IDPSSODescriptor.DEFAULT_ELEMENT_NAME, IDPSSODescriptor.TYPE_NAME);
    }

    public SPSSODescriptor createSPSSODescriptor() {
        return (SPSSODescriptor) openSamlBuilderFactory.getBuilder(SPSSODescriptor.DEFAULT_ELEMENT_NAME).buildObject(SPSSODescriptor.DEFAULT_ELEMENT_NAME, SPSSODescriptor.TYPE_NAME);
    }

    public AttributeAuthorityDescriptor createAttributeAuthorityDescriptor() {
        return (AttributeAuthorityDescriptor) openSamlBuilderFactory
                .getBuilder(AttributeAuthorityDescriptor.DEFAULT_ELEMENT_NAME)
                .buildObject(AttributeAuthorityDescriptor.DEFAULT_ELEMENT_NAME);
    }

    public AttributeService createAttributeService() {
        return (AttributeService) openSamlBuilderFactory
                .getBuilder(AttributeService.DEFAULT_ELEMENT_NAME)
                .buildObject(AttributeService.DEFAULT_ELEMENT_NAME);
    }

    public OrganizationDisplayName createOrganizationDisplayName(String name) {
        OrganizationDisplayName organizationDisplayName = (OrganizationDisplayName) openSamlBuilderFactory.getBuilder(OrganizationDisplayName.DEFAULT_ELEMENT_NAME).buildObject(OrganizationDisplayName.DEFAULT_ELEMENT_NAME, OrganizationDisplayName.TYPE_NAME);
        organizationDisplayName.setValue(name);
        organizationDisplayName.setXMLLang(IdaConstants.IDA_LANGUAGE);
        return organizationDisplayName;
    }

    public OrganizationName createOrganizationName(String name) {
        OrganizationName organizationName = (OrganizationName) openSamlBuilderFactory.getBuilder(OrganizationName.DEFAULT_ELEMENT_NAME).buildObject(OrganizationName.DEFAULT_ELEMENT_NAME, OrganizationName.TYPE_NAME);
        organizationName.setValue(name);
        organizationName.setXMLLang(IdaConstants.IDA_LANGUAGE);
        return organizationName;
    }

    public OrganizationURL createOrganizationUrl(String url) {
        OrganizationURL organizationUrl = (OrganizationURL) openSamlBuilderFactory.getBuilder(OrganizationURL.DEFAULT_ELEMENT_NAME).buildObject(OrganizationURL.DEFAULT_ELEMENT_NAME, OrganizationURL.TYPE_NAME);
        organizationUrl.setValue(url);
        organizationUrl.setXMLLang(IdaConstants.IDA_LANGUAGE);
        return organizationUrl;
    }

    public KeyDescriptor createKeyDescriptor(String use) {
        KeyDescriptor keyDescriptor = (KeyDescriptor) openSamlBuilderFactory.getBuilder(KeyDescriptor.DEFAULT_ELEMENT_NAME).buildObject(KeyDescriptor.DEFAULT_ELEMENT_NAME, KeyDescriptor.TYPE_NAME);
        keyDescriptor.setUse(UsageType.valueOf(use.toUpperCase()));
        return keyDescriptor;
    }

    public X509Certificate createX509Certificate(String cert) {
        X509Certificate x509Certificate = (X509Certificate) openSamlBuilderFactory.getBuilder(X509Certificate.DEFAULT_ELEMENT_NAME).buildObject(X509Certificate.DEFAULT_ELEMENT_NAME);
        x509Certificate.setValue(cert);
        return x509Certificate;
    }

    public X509Data createX509Data() {
        return (X509Data) openSamlBuilderFactory.getBuilder(X509Data.DEFAULT_ELEMENT_NAME).buildObject(X509Data.DEFAULT_ELEMENT_NAME, X509Data.TYPE_NAME);
    }

    public KeyInfo createKeyInfo(String keyNameValue) {
        final KeyInfo keyInfo = (KeyInfo) openSamlBuilderFactory.getBuilder(KeyInfo.DEFAULT_ELEMENT_NAME).buildObject(KeyInfo.DEFAULT_ELEMENT_NAME, KeyInfo.TYPE_NAME);
        if (keyNameValue != null) {
            KeyName keyName = createKeyName(keyNameValue);
            keyInfo.getKeyNames().add(keyName);
        }
        return keyInfo;
    }

    public KeyInfo createKeyInfo(final String issuerId, final String certificateValue) {
        KeyInfo keyInfo = createKeyInfo(issuerId);
        X509Data x509Data = createX509Data();
        final X509Certificate x509Certificate = createX509Certificate(certificateValue);
        x509Data.getX509Certificates().add(x509Certificate);
        keyInfo.getX509Datas().add(x509Data);
        return keyInfo;
    }

    private KeyName createKeyName(String keyNameValue) {
        final KeyName keyName = (KeyName) openSamlBuilderFactory.getBuilder(KeyName.DEFAULT_ELEMENT_NAME).buildObject(KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(keyNameValue);
        return keyName;
    }

    public Signature createSignature() {
        final XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
        final XMLObjectBuilder<?> builder = builderFactory.getBuilder(Signature.DEFAULT_ELEMENT_NAME);
        final XMLObject xmlObject = builder.buildObject(Signature.DEFAULT_ELEMENT_NAME);
        Signature signature = (Signature) xmlObject;

        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        return signature;
    }

    public Signature createSignature(@NotNull Credential signingCredential) {
        Signature signature = createSignature();
        signature.setSigningCredential(signingCredential);
        return signature;
    }

    public Signature createSignature(@NotNull SignatureAlgorithm signatureAlgorithm) {
        Signature signature = createSignature();
        signature.setSignatureAlgorithm(signatureAlgorithm.toString());
        return signature;
    }

    public Signature createSignature(@NotNull Credential signingCredential, @NotNull SignatureAlgorithm signatureAlgorithm) {
        Signature signature = createSignature(signingCredential);
        signature.setSignatureAlgorithm(signatureAlgorithm.toString());
        return signature;
    }

    public SAMLVersion createSamlVersion(String samlVersion) {
        return SAMLVersion.valueOf(samlVersion);
    }

    // There is a bug in OpenSaml where the type name for the element is wrong, hence the inline creation of the QName.
    public ContactPerson createContactPerson() {
        return (ContactPerson) openSamlBuilderFactory.getBuilder(ContactPerson.DEFAULT_ELEMENT_NAME).buildObject(ContactPerson.DEFAULT_ELEMENT_NAME, new QName(SAMLConstants.SAML20MD_NS, "ContactType", SAMLConstants.SAML20MD_PREFIX));
    }

    public EmailAddress createEmailAddress(String address) {
        EmailAddress emailAddress = (EmailAddress) openSamlBuilderFactory.getBuilder(EmailAddress.DEFAULT_ELEMENT_NAME).buildObject(EmailAddress.DEFAULT_ELEMENT_NAME);
        emailAddress.setAddress(address);
        return emailAddress;
    }

    public TelephoneNumber createTelephoneNumber(String number) {
        TelephoneNumber telephoneNumber = (TelephoneNumber) openSamlBuilderFactory.getBuilder(TelephoneNumber.DEFAULT_ELEMENT_NAME).buildObject(TelephoneNumber.DEFAULT_ELEMENT_NAME);
        telephoneNumber.setNumber(number);
        return telephoneNumber;
    }

    public GivenName createGivenName(String name) {
        GivenName givenName = (GivenName) openSamlBuilderFactory.getBuilder(GivenName.DEFAULT_ELEMENT_NAME).buildObject(GivenName.DEFAULT_ELEMENT_NAME);
        givenName.setName(name);
        return givenName;
    }

    public SurName createSurName(String name) {
        SurName surName = (SurName) openSamlBuilderFactory.getBuilder(SurName.DEFAULT_ELEMENT_NAME).buildObject(SurName.DEFAULT_ELEMENT_NAME);
        surName.setName(name);
        return surName;
    }

    public Company createCompany(String name) {
        Company company = (Company) openSamlBuilderFactory.getBuilder(Company.DEFAULT_ELEMENT_NAME).buildObject(Company.DEFAULT_ELEMENT_NAME);
        company.setName(name);
        return company;
    }

    public StringBasedMdsAttributeValue createSimpleMdsAttributeValue(String value) {
        StringBasedMdsAttributeValue stringBasedMdsAttributeValue = (StringBasedMdsAttributeValue) openSamlBuilderFactory.getBuilder(StringBasedMdsAttributeValue.TYPE_NAME).buildObject(StringBasedMdsAttributeValue.DEFAULT_ELEMENT_NAME, StringBasedMdsAttributeValue.TYPE_NAME);
        stringBasedMdsAttributeValue.setValue(value);
        return stringBasedMdsAttributeValue;
    }

    public IdpFraudEventId createIdpFraudEventAttributeValue(String fraudEventId) {
        IdpFraudEventId idpFraudEventId = (IdpFraudEventId) openSamlBuilderFactory.getBuilder(IdpFraudEventId.TYPE_NAME).buildObject(IdpFraudEventId.DEFAULT_ELEMENT_NAME, IdpFraudEventId.TYPE_NAME);
        idpFraudEventId.setValue(fraudEventId);
        return idpFraudEventId;
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

    public StatusValue createStatusValue(String value) {
        StatusValue statusValue = (StatusValue) openSamlBuilderFactory.getBuilder(StatusValue.DEFAULT_ELEMENT_NAME).buildObject(StatusValue.DEFAULT_ELEMENT_NAME);
        statusValue.setValue(value);
        return statusValue;
    }


    public StatusDetail createStatusDetail() {
        StatusDetail statusDetail = (StatusDetail) openSamlBuilderFactory.getBuilder(StatusDetail.DEFAULT_ELEMENT_NAME).buildObject(StatusDetail.DEFAULT_ELEMENT_NAME);
        return statusDetail;
    }
}

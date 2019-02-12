package uk.gov.ida.saml.security.saml.builders;

import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.Scoping;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA256;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA256;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import uk.gov.ida.saml.core.extensions.IdaAuthnContext;
import uk.gov.ida.saml.security.saml.TestSamlObjectFactory;

import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Throwables.propagate;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;


public class AuthnRequestBuilder {

    public static final String RANDOM_UUID = "_" + UUID.randomUUID().toString();
    private TestSamlObjectFactory testSamlObjectFactory = new TestSamlObjectFactory();

    private boolean shouldSign = true;
    private boolean shouldAddSignature = true;
    private SignatureAlgorithm signatureAlgorithm = new SignatureRSASHA256();
    private DigestAlgorithm digestAlgorithm = new DigestSHA256();

    private static final String SAML_VERSION_NUMBER = "2.0";


    private Optional<NameIDPolicy> nameIdPolicy = empty();
    private Optional<Scoping> scoping = empty();
    private Optional<String> assertionConsumerServiceUrl = empty();
    private Optional<String> protocolBinding = ofNullable(SAMLConstants.SAML2_POST_BINDING_URI);
    private Optional<Boolean> isPassive = empty();

    private Optional<Issuer> issuer = ofNullable(IssuerBuilder.anIssuer().build());
    private Optional<String> id = ofNullable(RANDOM_UUID);
    private Optional<DateTime> issueInstant = ofNullable(DateTime.now());

    private Optional<String> destination = empty();
    private Optional<Credential> signingCredential = empty();
    private Optional<Boolean> forceAuthn = empty();
    private Optional<Integer> assertionConsumerServiceIndex = empty();

    public static AuthnRequestBuilder anAuthnRequest() {
        return new AuthnRequestBuilder();
    }

    public AuthnRequest build() {
        AuthnRequest authnRequest = testSamlObjectFactory.createAuthnRequest();
        issuer.ifPresent(authnRequest::setIssuer);
        id.ifPresent(authnRequest::setID);

        authnRequest.setVersion(testSamlObjectFactory.createSamlVersion(SAML_VERSION_NUMBER));

        authnRequest.setRequestedAuthnContext(testSamlObjectFactory.createRequestedAuthnContext(AuthnContextComparisonTypeEnumeration.MINIMUM));
        testSamlObjectFactory.createRequestedAuthnContext(AuthnContextComparisonTypeEnumeration.MINIMUM).getAuthnContextClassRefs().add(testSamlObjectFactory.createAuthnContextClassReference(IdaAuthnContext.LEVEL_1_AUTHN_CTX));
        testSamlObjectFactory.createRequestedAuthnContext(AuthnContextComparisonTypeEnumeration.MINIMUM).getAuthnContextClassRefs().add(testSamlObjectFactory.createAuthnContextClassReference(IdaAuthnContext.LEVEL_2_AUTHN_CTX));

        nameIdPolicy.ifPresent(authnRequest::setNameIDPolicy);
        scoping.ifPresent(authnRequest::setScoping);
        assertionConsumerServiceUrl.ifPresent(authnRequest::setAssertionConsumerServiceURL);
        protocolBinding.ifPresent(authnRequest::setProtocolBinding);
        isPassive.ifPresent(authnRequest::setIsPassive);
        issueInstant.ifPresent(authnRequest::setIssueInstant);
        destination.ifPresent(authnRequest::setDestination);
        forceAuthn.ifPresent(authnRequest::setForceAuthn);
        assertionConsumerServiceIndex.ifPresent(authnRequest::setAssertionConsumerServiceIndex);

        //This must be the last thing done before returning; otherwise, the signature will be invalidated
        if (issuer.isPresent() && !Strings.isNullOrEmpty(issuer.get().getValue()) && shouldAddSignature) {
            final SignatureBuilder signatureBuilder = SignatureBuilder.aSignature().withSignatureAlgorithm(signatureAlgorithm);
            id.ifPresent(s -> signatureBuilder.withDigestAlgorithm(s, digestAlgorithm));
            signingCredential.ifPresent(signatureBuilder::withSigningCredential);
            authnRequest.setSignature(signatureBuilder.build());
            try {
                XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(authnRequest).marshall(authnRequest);
                if (shouldSign) {
                    Signer.signObject(authnRequest.getSignature());
                }
            } catch (SignatureException | MarshallingException e) {
                throw propagate(e);
            }
        }

        return authnRequest;
    }

    public AuthnRequestBuilder withoutSignatureElement() {
        shouldAddSignature = false;
        return this;
    }

    public AuthnRequestBuilder withoutSigning() {
        shouldSign = false;
        return this;
    }

    public AuthnRequestBuilder withIssuer(Issuer issuer) {
        this.issuer = ofNullable(issuer);
        return this;
    }

    public AuthnRequestBuilder withSigningCredential(Credential credential) {
        this.signingCredential = ofNullable(credential);
        this.shouldAddSignature = true;
        return this;
    }

}

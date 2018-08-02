package uk.gov.ida.apprule.support;

import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.Scoping;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.extensions.IdaAuthnContext;
import uk.gov.ida.saml.core.test.AuthnRequestIdGenerator;
import uk.gov.ida.saml.core.test.builders.IssuerBuilder;
import uk.gov.ida.saml.core.test.builders.SignatureBuilder;
import uk.gov.ida.saml.idp.test.builders.ConditionsBuilder;

import java.util.Optional;

import static com.google.common.base.Throwables.propagate;

public class AuthnRequestBuilder {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
    private Optional<NameIDPolicy> nameIdPolicy = Optional.empty();
    private Optional<Scoping> scoping = Optional.empty();
    private Optional<String> assertionConsumerServiceUrl = Optional.empty();
    private Optional<String> protocolBinding = Optional.ofNullable(SAMLConstants.SAML2_POST_BINDING_URI);
    private Optional<Boolean> isPassive = Optional.empty();

    private Optional<Issuer> issuer = Optional.ofNullable(IssuerBuilder.anIssuer().build());
    private Optional<String> id = Optional.ofNullable(AuthnRequestIdGenerator.generateRequestId());
    private Optional<String> minimumLevelOfAssurance = Optional.ofNullable(IdaAuthnContext.LEVEL_1_AUTHN_CTX);
    private boolean shouldSign = true;
    private boolean shouldAddSignature = true;
    private Optional<DateTime> issueInstant = Optional.ofNullable(DateTime.now());

    private Optional<String> versionNumber = Optional.ofNullable(IdaConstants.SAML_VERSION_NUMBER);
    private Optional<String> destination = Optional.empty();
    private Optional<String> requiredLevelOfAssurance = Optional.ofNullable(IdaAuthnContext.LEVEL_2_AUTHN_CTX);
    private Optional<Credential> signingCredential = Optional.empty();
    private Optional<Boolean> forceAuthn = Optional.empty();
    private Optional<Integer> assertionConsumerServiceIndex = Optional.empty();

    public static AuthnRequestBuilder anAuthnRequest() {
        return new AuthnRequestBuilder();
    }

    public AuthnRequest build() {

        AuthnRequest authnRequest = openSamlXmlObjectFactory.createAuthnRequest();

        issuer.ifPresent(authnRequest::setIssuer);
        id.ifPresent(authnRequest::setID);

        if (versionNumber.isPresent()) {
            authnRequest.setVersion(openSamlXmlObjectFactory.createSamlVersion(versionNumber.get()));
        } else {
            authnRequest.setVersion(null);
        }

        if (minimumLevelOfAssurance.isPresent() || requiredLevelOfAssurance.isPresent()) {
            RequestedAuthnContext requestedAuthnContext = openSamlXmlObjectFactory.createRequestedAuthnContext(AuthnContextComparisonTypeEnumeration.MINIMUM);
            authnRequest.setRequestedAuthnContext(requestedAuthnContext);
            if (minimumLevelOfAssurance.isPresent()) {
                AuthnContextClassRef authnContextClassReference = openSamlXmlObjectFactory.createAuthnContextClassReference(minimumLevelOfAssurance.get());
                requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassReference);
            }
            if (requiredLevelOfAssurance.isPresent()) {
                AuthnContextClassRef authnContextClassReference = openSamlXmlObjectFactory.createAuthnContextClassReference(requiredLevelOfAssurance.get());
                requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassReference);
            }
        }

        nameIdPolicy.ifPresent(authnRequest::setNameIDPolicy);
        scoping.ifPresent(authnRequest::setScoping);
        assertionConsumerServiceUrl.ifPresent(authnRequest::setAssertionConsumerServiceURL);
        protocolBinding.ifPresent(authnRequest::setProtocolBinding);
        isPassive.ifPresent(authnRequest::setIsPassive);
        issueInstant.ifPresent(authnRequest::setIssueInstant);
        destination.ifPresent(authnRequest::setDestination);
        forceAuthn.ifPresent(authnRequest::setForceAuthn);
        assertionConsumerServiceIndex.ifPresent(authnRequest::setAssertionConsumerServiceIndex);

        authnRequest.setConditions(ConditionsBuilder.aConditions().build());

        //This must be the last thing done before returning; otherwise, the signature will be invalidated
        if (issuer.isPresent() && !Strings.isNullOrEmpty(issuer.get().getValue()) && shouldAddSignature) {
            final SignatureBuilder signatureBuilder = SignatureBuilder.aSignature();
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

    public AuthnRequestBuilder withId(String id) {
        this.id = Optional.ofNullable(id);
        return this;
    }

    public AuthnRequestBuilder withIssuer(Issuer issuer) {
        this.issuer = Optional.ofNullable(issuer);
        return this;
    }

    public AuthnRequestBuilder withNameIdPolicy(NameIDPolicy policy) {
        this.nameIdPolicy = Optional.of(policy);
        return this;
    }

    public AuthnRequestBuilder withScoping(Scoping scoping) {
        this.scoping = Optional.ofNullable(scoping);
        return this;
    }

    public AuthnRequestBuilder withAssertionConsumerServiceUrl(String url) {
        this.assertionConsumerServiceUrl = Optional.ofNullable(url);
        return this;
    }

    public AuthnRequestBuilder withProtocolBinding(String protocolBinding) {
        this.protocolBinding = Optional.ofNullable(protocolBinding);
        return this;
    }

    public AuthnRequestBuilder withIsPassive(boolean isPassive) {
        this.isPassive = Optional.ofNullable(isPassive);
        return this;
    }

    public AuthnRequestBuilder withIssueInstant(DateTime dateTime) {
        this.issueInstant = Optional.ofNullable(dateTime);
        return this;
    }

    public AuthnRequestBuilder withVersionNumber(String versionNumber) {
        this.versionNumber = Optional.ofNullable(versionNumber);
        return this;
    }

    public AuthnRequestBuilder withDestination(String destination) {
        this.destination = Optional.ofNullable(destination);
        return this;
    }

    public AuthnRequestBuilder withSigningCredential(Credential credential) {
        this.signingCredential = Optional.ofNullable(credential);
        this.shouldAddSignature = true;
        return this;
    }

    public AuthnRequestBuilder withForceAuthn(Boolean forceAuthn) {
        this.forceAuthn = Optional.of(forceAuthn);
        return this;
    }

    public void withAssertionConsumerServiceIndex(Integer index) {
        this.assertionConsumerServiceIndex = Optional.of(index);
    }
}

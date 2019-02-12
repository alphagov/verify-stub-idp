package uk.gov.ida.saml.core.test.builders;

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
import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA256;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA256;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.extensions.IdaAuthnContext;
import uk.gov.ida.saml.core.test.AuthnRequestIdGenerator;
import uk.gov.ida.saml.core.test.OpenSamlXmlObjectFactory;

import javax.validation.constraints.NotNull;
import java.util.Optional;

import static com.google.common.base.Throwables.propagate;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;


public class AuthnRequestBuilder {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();

    private boolean shouldSign = true;
    private boolean shouldAddSignature = true;
    private SignatureAlgorithm signatureAlgorithm = new SignatureRSASHA256();
    private DigestAlgorithm digestAlgorithm = new DigestSHA256();

    private Optional<NameIDPolicy> nameIdPolicy = empty();
    private Optional<Scoping> scoping = empty();
    private Optional<String> assertionConsumerServiceUrl = empty();
    private Optional<String> protocolBinding = ofNullable(SAMLConstants.SAML2_POST_BINDING_URI);
    private Optional<Boolean> isPassive = empty();

    private Optional<Issuer> issuer = ofNullable(IssuerBuilder.anIssuer().build());
    private Optional<String> id = ofNullable(AuthnRequestIdGenerator.generateRequestId());
    private Optional<String> minimumLevelOfAssurance = ofNullable(IdaAuthnContext.LEVEL_1_AUTHN_CTX);
    private Optional<DateTime> issueInstant = ofNullable(DateTime.now());

    private Optional<String> versionNumber = ofNullable(IdaConstants.SAML_VERSION_NUMBER);
    private Optional<String> destination = empty();
    private Optional<String> requiredLevelOfAssurance = ofNullable(IdaAuthnContext.LEVEL_2_AUTHN_CTX);
    private Optional<Credential> signingCredential = empty();
    private Optional<Boolean> forceAuthn = empty();
    private Optional<Integer> assertionConsumerServiceIndex = empty();

    public static AuthnRequestBuilder anAuthnRequest() {
        return new AuthnRequestBuilder();
    }

    public AuthnRequest build() {
        AuthnRequest authnRequest = openSamlXmlObjectFactory.createAuthnRequest();

        if (issuer.isPresent()) {
            authnRequest.setIssuer(issuer.get());
        }

        if (id.isPresent()) {
            authnRequest.setID(id.get());
        }

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

        if (nameIdPolicy.isPresent()) {
            authnRequest.setNameIDPolicy(nameIdPolicy.get());
        }

        if (scoping.isPresent()) {
            authnRequest.setScoping(scoping.get());
        }

        if (assertionConsumerServiceUrl.isPresent()) {
            authnRequest.setAssertionConsumerServiceURL(assertionConsumerServiceUrl.get());
        }

        if (protocolBinding.isPresent()) {
            authnRequest.setProtocolBinding(protocolBinding.get());
        }

        if (isPassive.isPresent()) {
            authnRequest.setIsPassive(isPassive.get());
        }

        if (issueInstant.isPresent()) {
            authnRequest.setIssueInstant(issueInstant.get());
        }

        if (destination.isPresent()) {
            authnRequest.setDestination(destination.get());
        }

        if (forceAuthn.isPresent()) {
            authnRequest.setForceAuthn(forceAuthn.get());
        }

        if(assertionConsumerServiceIndex.isPresent()) {
            authnRequest.setAssertionConsumerServiceIndex(assertionConsumerServiceIndex.get());
        }

        //This must be the last thing done before returning; otherwise, the signature will be invalidated
        if (issuer.isPresent() && !Strings.isNullOrEmpty(issuer.get().getValue()) && shouldAddSignature) {
            final SignatureBuilder signatureBuilder = SignatureBuilder.aSignature().withSignatureAlgorithm(signatureAlgorithm);
            if (id.isPresent()) {
                signatureBuilder.withDigestAlgorithm(id.get(), digestAlgorithm);
            }
            if (signingCredential.isPresent()) {
                signatureBuilder.withSigningCredential(signingCredential.get());
            }
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
        this.id = ofNullable(id);
        return this;
    }

    public AuthnRequestBuilder withIssuer(Issuer issuer) {
        this.issuer = ofNullable(issuer);
        return this;
    }

    public AuthnRequestBuilder withNameIdPolicy(NameIDPolicy policy) {
        this.nameIdPolicy = ofNullable(policy);
        return this;
    }

    public AuthnRequestBuilder withScoping(Scoping scoping) {
        this.scoping = ofNullable(scoping);
        return this;
    }

    public AuthnRequestBuilder withAssertionConsumerServiceUrl(String url) {
        this.assertionConsumerServiceUrl = ofNullable(url);
        return this;
    }

    public AuthnRequestBuilder withProtocolBinding(String protocolBinding) {
        this.protocolBinding = ofNullable(protocolBinding);
        return this;
    }

    public AuthnRequestBuilder withIsPassive(boolean isPassive) {
        this.isPassive = ofNullable(isPassive);
        return this;
    }

    public AuthnRequestBuilder withIssueInstant(DateTime dateTime) {
        this.issueInstant = ofNullable(dateTime);
        return this;
    }

    public AuthnRequestBuilder withVersionNumber(String versionNumber) {
        this.versionNumber = ofNullable(versionNumber);
        return this;
    }

    public AuthnRequestBuilder withDestination(String destination) {
        this.destination = ofNullable(destination);
        return this;
    }

    public AuthnRequestBuilder withSigningCredential(Credential credential) {
        this.signingCredential = ofNullable(credential);
        this.shouldAddSignature = true;
        return this;
    }

    public AuthnRequestBuilder withForceAuthn(Boolean forceAuthn) {
        this.forceAuthn = Optional.of(forceAuthn);
        return this;
    }

    public AuthnRequestBuilder withSignatureAlgorithm(@NotNull SignatureAlgorithm signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
        return this;
    }

    public AuthnRequestBuilder withDigestAlgorithm(@NotNull DigestAlgorithm digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
        return this;
    }

    public void withAssertionConsumerServiceIndex(Integer index) {
        this.assertionConsumerServiceIndex = Optional.of(index);
    }

}

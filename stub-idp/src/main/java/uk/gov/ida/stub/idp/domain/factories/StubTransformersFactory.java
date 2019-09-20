package uk.gov.ida.stub.idp.domain.factories;

import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.api.CoreTransformersFactory;
import uk.gov.ida.saml.core.transformers.outbound.OutboundAssertionToSubjectTransformer;
import uk.gov.ida.saml.core.transformers.outbound.ResponseToSignedStringTransformer;
import uk.gov.ida.saml.deserializers.StringToOpenSamlObjectTransformer;
import uk.gov.ida.saml.hub.domain.IdaAuthnRequestFromHub;
import uk.gov.ida.saml.hub.factories.AttributeFactory_1_1;
import uk.gov.ida.saml.hub.transformers.inbound.decorators.AuthnRequestSizeValidator;
import uk.gov.ida.saml.hub.validators.StringSizeValidator;
import uk.gov.ida.saml.idp.stub.transformers.inbound.AuthnRequestToIdaRequestFromHubTransformer;
import uk.gov.ida.saml.idp.stub.transformers.inbound.IdaAuthnRequestFromHubUnmarshaller;
import uk.gov.ida.saml.idp.stub.transformers.outbound.IdentityProviderAssertionToAssertionTransformer;
import uk.gov.ida.saml.idp.stub.transformers.outbound.IdentityProviderAuthnStatementToAuthnStatementTransformer;
import uk.gov.ida.saml.security.EncryptionKeyStore;
import uk.gov.ida.saml.security.EntityToEncryptForLocator;
import uk.gov.ida.saml.security.IdaKeyStore;
import uk.gov.ida.saml.security.SigningKeyStore;
import uk.gov.ida.stub.idp.domain.IdpIdaStatusMarshaller;
import uk.gov.ida.stub.idp.domain.OutboundResponseFromIdp;
import uk.gov.ida.stub.idp.saml.transformers.OutboundResponseFromIdpToSamlResponseTransformer;

import java.util.function.Function;

public class StubTransformersFactory {

    private CoreTransformersFactory coreTransformersFactory;


    public StubTransformersFactory() {
        this.coreTransformersFactory = new CoreTransformersFactory();
    }

    public Function<String, IdaAuthnRequestFromHub> getStringToIdaAuthnRequestFromHub(
            final SigningKeyStore signingKeyStore){
        AuthnRequestSizeValidator sizeValidator = new AuthnRequestSizeValidator(new StringSizeValidator());


        StringToOpenSamlObjectTransformer<AuthnRequest> stringtoOpenSamlObjectTransformer = coreTransformersFactory.getStringtoOpenSamlObjectTransformer(sizeValidator);

        return getAuthnRequestToIdaRequestFromHubTransformer(signingKeyStore).compose(stringtoOpenSamlObjectTransformer);
    }

    public Function<String, AuthnRequest> getStringToAuthnRequest(){
        AuthnRequestSizeValidator sizeValidator = new AuthnRequestSizeValidator(new StringSizeValidator());

        return coreTransformersFactory.getStringtoOpenSamlObjectTransformer(sizeValidator);
    }

    private AuthnRequestToIdaRequestFromHubTransformer getAuthnRequestToIdaRequestFromHubTransformer(SigningKeyStore signingKeyStore) {
        return new AuthnRequestToIdaRequestFromHubTransformer(
                new IdaAuthnRequestFromHubUnmarshaller(),
                coreTransformersFactory.getSamlRequestSignatureValidator(signingKeyStore)
        );
    }

    public Function<OutboundResponseFromIdp, String> getOutboundResponseFromIdpToStringTransformer(
            final EncryptionKeyStore publicKeyStore,
            final IdaKeyStore keyStore,
            EntityToEncryptForLocator entityToEncryptForLocator,
            String publicSigningKey,
            String issuerId,
            SignatureAlgorithm signatureAlgorithm,
            DigestAlgorithm digestAlgorithm
    ){
        return coreTransformersFactory.getResponseStringTransformer(
                publicKeyStore,
                keyStore,
                entityToEncryptForLocator,
                publicSigningKey,
                issuerId,
                signatureAlgorithm,
                digestAlgorithm).compose(getOutboundResponseFromIdpToSamlResponseTransformer());
    }

    public Function<OutboundResponseFromIdp, String> getOutboundResponseFromIdpToStringTransformer(
            final EncryptionKeyStore encryptionKeyStore,
            final IdaKeyStore keyStore,
            EntityToEncryptForLocator entityToEncryptForLocator,
            SignatureAlgorithm signatureAlgorithm,
            DigestAlgorithm digestAlgorithm
    ){
        ResponseToSignedStringTransformer responseStringTransformer = coreTransformersFactory.getResponseStringTransformer(
                encryptionKeyStore,
                keyStore,
                entityToEncryptForLocator,
                signatureAlgorithm,
                digestAlgorithm);

        return responseStringTransformer.compose(getOutboundResponseFromIdpToSamlResponseTransformer());
    }

    public OutboundResponseFromIdpToSamlResponseTransformer getOutboundResponseFromIdpToSamlResponseTransformer() {
        OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
        return new OutboundResponseFromIdpToSamlResponseTransformer(
                new IdpIdaStatusMarshaller(openSamlXmlObjectFactory),
                openSamlXmlObjectFactory,
                getIdpAssertionToAssertionTransformer()
        );
    }

    private IdentityProviderAssertionToAssertionTransformer getIdpAssertionToAssertionTransformer() {
        OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
        return new IdentityProviderAssertionToAssertionTransformer(
                openSamlXmlObjectFactory,
                new AttributeFactory_1_1(openSamlXmlObjectFactory),
                new IdentityProviderAuthnStatementToAuthnStatementTransformer(openSamlXmlObjectFactory),
                new OutboundAssertionToSubjectTransformer(openSamlXmlObjectFactory)
        );
    }

}

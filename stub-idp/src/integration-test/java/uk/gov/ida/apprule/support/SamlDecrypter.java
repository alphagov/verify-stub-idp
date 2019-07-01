package uk.gov.ida.apprule.support;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.slf4j.event.Level;
import uk.gov.ida.apprule.support.eidas.EidasAttributeStatementAssertionValidator;
import uk.gov.ida.apprule.support.eidas.EidasAuthnResponseIssuerValidator;
import uk.gov.ida.apprule.support.eidas.InboundResponseFromCountry;
import uk.gov.ida.common.shared.security.PrivateKeyFactory;
import uk.gov.ida.common.shared.security.PublicKeyFactory;
import uk.gov.ida.common.shared.security.X509CertificateFactory;
import uk.gov.ida.saml.core.errors.SamlTransformationErrorFactory;
import uk.gov.ida.saml.core.transformers.AuthnContextFactory;
import uk.gov.ida.saml.core.validation.SamlTransformationErrorException;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;
import uk.gov.ida.saml.core.validation.assertion.AssertionAttributeStatementValidator;
import uk.gov.ida.saml.core.validation.assertion.IdentityProviderAssertionValidator;
import uk.gov.ida.saml.core.validation.subjectconfirmation.AssertionSubjectConfirmationValidator;
import uk.gov.ida.saml.core.validators.DestinationValidator;
import uk.gov.ida.saml.core.validators.assertion.AuthnStatementAssertionValidator;
import uk.gov.ida.saml.core.validators.assertion.DuplicateAssertionValidatorImpl;
import uk.gov.ida.saml.core.validators.assertion.IPAddressValidator;
import uk.gov.ida.saml.core.validators.assertion.MatchingDatasetAssertionValidator;
import uk.gov.ida.saml.core.validators.subject.AssertionSubjectValidator;
import uk.gov.ida.saml.deserializers.OpenSamlXMLObjectUnmarshaller;
import uk.gov.ida.saml.deserializers.StringToOpenSamlObjectTransformer;
import uk.gov.ida.saml.deserializers.parser.SamlObjectParser;
import uk.gov.ida.saml.deserializers.validators.Base64StringDecoder;
import uk.gov.ida.saml.deserializers.validators.NotNullSamlStringValidator;
import uk.gov.ida.saml.hub.domain.InboundResponseFromIdp;
import uk.gov.ida.saml.hub.transformers.inbound.IdaResponseFromIdpUnmarshaller;
import uk.gov.ida.saml.hub.transformers.inbound.IdpIdaStatusUnmarshaller;
import uk.gov.ida.saml.hub.transformers.inbound.PassthroughAssertionUnmarshaller;
import uk.gov.ida.saml.hub.transformers.inbound.SamlStatusToIdaStatusCodeMapper;
import uk.gov.ida.saml.hub.transformers.inbound.providers.DecoratedSamlResponseToIdaResponseIssuedByIdpTransformer;
import uk.gov.ida.saml.hub.validators.StringSizeValidator;
import uk.gov.ida.saml.hub.validators.authnrequest.ConcurrentMapIdExpirationCache;
import uk.gov.ida.saml.hub.validators.response.common.ResponseSizeValidator;
import uk.gov.ida.saml.hub.validators.response.idp.components.EncryptedResponseFromIdpValidator;
import uk.gov.ida.saml.hub.validators.response.idp.components.ResponseAssertionsFromIdpValidator;
import uk.gov.ida.saml.metadata.IdpMetadataPublicKeyStore;
import uk.gov.ida.saml.metadata.JerseyClientMetadataResolver;
import uk.gov.ida.saml.metadata.factories.MetadataSignatureTrustEngineFactory;
import uk.gov.ida.saml.security.AssertionDecrypter;
import uk.gov.ida.saml.security.CredentialFactorySignatureValidator;
import uk.gov.ida.saml.security.DecrypterFactory;
import uk.gov.ida.saml.security.IdaKeyStore;
import uk.gov.ida.saml.security.IdaKeyStoreCredentialRetriever;
import uk.gov.ida.saml.security.MetadataBackedSignatureValidator;
import uk.gov.ida.saml.security.SamlAssertionsSignatureValidator;
import uk.gov.ida.saml.security.SamlMessageSignatureValidator;
import uk.gov.ida.saml.security.SigningCredentialFactory;
import uk.gov.ida.saml.security.validators.ValidatedResponse;
import uk.gov.ida.saml.security.validators.encryptedelementtype.EncryptionAlgorithmValidator;
import uk.gov.ida.saml.security.validators.issuer.IssuerValidator;
import uk.gov.ida.saml.security.validators.signature.SamlResponseSignatureValidator;
import uk.gov.ida.saml.serializers.XmlObjectToBase64EncodedStringTransformer;

import javax.ws.rs.client.Client;
import java.net.URI;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.text.MessageFormat.format;
import static java.util.Optional.ofNullable;
import static uk.gov.ida.saml.core.test.TestCertificateStrings.HUB_TEST_PRIVATE_ENCRYPTION_KEY;
import static uk.gov.ida.saml.core.test.TestCertificateStrings.HUB_TEST_PUBLIC_ENCRYPTION_CERT;

/**
 * Be warned that this class does little to no validation and is just for testing the contents of a response
 */
public class SamlDecrypter {

    private final Client client;
    private final URI metadataUri;
    private final String hubEntityId;
    private final int localPort;

    // Manual Guice injection
    private final StringToOpenSamlObjectTransformer<Response> stringToOpenSamlObjectTransformer = new StringToOpenSamlObjectTransformer(new NotNullSamlStringValidator(),
            new Base64StringDecoder(),
            new ResponseSizeValidator(new StringSizeValidator()),
            new OpenSamlXMLObjectUnmarshaller(new SamlObjectParser()));
    private final Optional<String> eidasSchemeName;

    public SamlDecrypter(Client client, URI metadataUri, String hubEntityId, int localPort, Optional<String> eidasSchemeName) {
        this.client = client;
        this.metadataUri = metadataUri;
        this.hubEntityId = hubEntityId;
        this.localPort = localPort;
        this.eidasSchemeName = eidasSchemeName;
    }

    /**
     * Be warned that this method does little to no validation and is just for testing the contents of a response
     */
    public InboundResponseFromIdp decryptSaml(String samlResponse) {
        final JerseyClientMetadataResolver jerseyClientMetadataResolver = getMetadataResolver(metadataUri);
        final SigningCredentialFactory credentialFactory = new SigningCredentialFactory(new AuthnResponseKeyStore(new IdpMetadataPublicKeyStore(jerseyClientMetadataResolver)));
        DecoratedSamlResponseToIdaResponseIssuedByIdpTransformer decoratedSamlResponseToIdaResponseIssuedByIdpTransformer
                = buildDecoratedSamlResponseToIdaResponseIssuedByIdpTransformer(credentialFactory, createHubKeyStore());

        final org.opensaml.saml.saml2.core.Response response = stringToOpenSamlObjectTransformer.apply(samlResponse);
        return decoratedSamlResponseToIdaResponseIssuedByIdpTransformer.apply(response);
    }

    private JerseyClientMetadataResolver getMetadataResolver(URI metadataUri) {
        final JerseyClientMetadataResolver jerseyClientMetadataResolver = new JerseyClientMetadataResolver(null, client, metadataUri);
        try {
            // a parser pool needs to be provided
            BasicParserPool pool = new BasicParserPool();
            pool.initialize();
            jerseyClientMetadataResolver.setParserPool(pool);
            jerseyClientMetadataResolver.setId("SamlDecrypter.MetadataResolver" + UUID.randomUUID());
            jerseyClientMetadataResolver.initialize();
            jerseyClientMetadataResolver.refresh();
        } catch (ComponentInitializationException | ResolverException e) {
            e.printStackTrace();
        }
        return jerseyClientMetadataResolver;
    }

    /**
     * Be warned that this method does little to no validation and is just for testing the contents of a response
     */
    public InboundResponseFromCountry decryptEidasSaml(String samlResponse) {

        Response response = stringToOpenSamlObjectTransformer.apply(samlResponse);
        ValidatedResponse validatedResponse = validateResponse(response);
        AssertionDecrypter assertionDecrypter = getAES256WithGCMAssertionDecrypter(createHubKeyStore());
        List<Assertion> assertions = assertionDecrypter.decryptAssertions(validatedResponse);
        Optional<Assertion> validatedIdentityAssertion = validateAssertion(validatedResponse, assertions);

        return new InboundResponseFromCountry(response.getIssuer().getValue(),
                validatedIdentityAssertion.get(),
                response.getStatus()
        );
    }

    private AssertionDecrypter getAES256WithGCMAssertionDecrypter(IdaKeyStore keyStore) {
        IdaKeyStoreCredentialRetriever idaKeyStoreCredentialRetriever = new IdaKeyStoreCredentialRetriever(keyStore);
        Decrypter decrypter = new DecrypterFactory().createDecrypter(idaKeyStoreCredentialRetriever.getDecryptingCredentials());
        return new AssertionDecrypter(
                new EncryptionAlgorithmValidator(
                        ImmutableSet.of(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256, EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM),
                        ImmutableSet.of(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP)),
                decrypter
        );
    }

    private ValidatedResponse validateResponse(Response response) {
        SamlResponseSignatureValidator samlResponseSignatureValidator = new SamlResponseSignatureValidator(getSamlMessageSignatureValidator(response.getIssuer().getValue()));
        final ValidatedResponse validatedResponse = samlResponseSignatureValidator.validate(response, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        new DestinationValidator(URI.create("http://foo.com/bar"), "/bar").validate(response.getDestination());
        return validatedResponse;
    }

    private void getValidatedAssertion(ValidatedResponse validatedResponse, List<Assertion> decryptedAssertions) {
        SamlAssertionsSignatureValidator samlAssertionsSignatureValidator = new SamlAssertionsSignatureValidator(getSamlMessageSignatureValidator(validatedResponse.getIssuer().getValue()));
        samlAssertionsSignatureValidator.validate(decryptedAssertions, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
    }

    public void responseAssertionFromCountryValidatorValidate(ValidatedResponse validatedResponse, Assertion validatedIdentityAssertion) {

        new IdentityProviderAssertionValidator(
                new IssuerValidator(),
                new AssertionSubjectValidator(),
                new AssertionAttributeStatementValidator(),
                new AssertionSubjectConfirmationValidator()
        ).validate(validatedIdentityAssertion, validatedResponse.getInResponseTo(), "http://foo.com/bar");

        if (validatedResponse.isSuccess()) {

            if (validatedIdentityAssertion.getAuthnStatements().size() > 1) {
                SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.multipleAuthnStatements();
                throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
            }

            new EidasAttributeStatementAssertionValidator().validate(validatedIdentityAssertion);
            new AuthnStatementAssertionValidator(
                    new DuplicateAssertionValidatorImpl(new ConcurrentMapIdExpirationCache<>(new ConcurrentHashMap<>()))
            ).validate(validatedIdentityAssertion);
            new EidasAuthnResponseIssuerValidator().validate(validatedResponse, validatedIdentityAssertion);
        }
    }

    private SamlMessageSignatureValidator getSamlMessageSignatureValidator(String entityId) {
        return ofNullable(getMetadataResolver(URI.create("http://localhost:" + localPort + "/" + eidasSchemeName.get() + "/ServiceMetadata")))
                .map(m -> {
                    try {
                        return new MetadataSignatureTrustEngineFactory().createSignatureTrustEngine(m);
                    } catch (ComponentInitializationException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(MetadataBackedSignatureValidator::withoutCertificateChainValidation)
                .map(SamlMessageSignatureValidator::new)
                .orElseThrow(() -> new SamlTransformationErrorException(format("Unable to find metadata resolver for entity Id {0}", entityId), Level.ERROR));
    }

    private Optional<Assertion> validateAssertion(ValidatedResponse validatedResponse, List<Assertion> decryptedAssertions) {
        getValidatedAssertion(validatedResponse, decryptedAssertions);
        Optional<Assertion> identityAssertion = decryptedAssertions.stream().findFirst();
        identityAssertion.ifPresent(assertion -> responseAssertionFromCountryValidatorValidate(validatedResponse, assertion));
        return identityAssertion;
    }


    public IdaKeyStore createHubKeyStore() {
        PrivateKey privateKey = new PrivateKeyFactory().createPrivateKey(Base64.getDecoder().decode(HUB_TEST_PRIVATE_ENCRYPTION_KEY));

        PublicKey publicKey = new PublicKeyFactory(new X509CertificateFactory()).createPublicKey(HUB_TEST_PUBLIC_ENCRYPTION_CERT);

        List<KeyPair> encryptionKeys = ImmutableList.of(new KeyPair(publicKey, privateKey));
        return new IdaKeyStore(null, encryptionKeys);
    }


    private DecoratedSamlResponseToIdaResponseIssuedByIdpTransformer buildDecoratedSamlResponseToIdaResponseIssuedByIdpTransformer(SigningCredentialFactory credentialFactory, IdaKeyStore keyStore) {
        IdaKeyStoreCredentialRetriever storeCredentialRetriever = new IdaKeyStoreCredentialRetriever(keyStore);
        return new DecoratedSamlResponseToIdaResponseIssuedByIdpTransformer(
                new IdaResponseFromIdpUnmarshaller(
                        new IdpIdaStatusUnmarshaller(),
                        new PassthroughAssertionUnmarshaller(new XmlObjectToBase64EncodedStringTransformer<>(), new AuthnContextFactory())),
                new SamlResponseSignatureValidator(new SamlMessageSignatureValidator(new CredentialFactorySignatureValidator(credentialFactory))),
                new AssertionDecrypter(new EncryptionAlgorithmValidator(), new DecrypterFactory().createDecrypter(storeCredentialRetriever.getDecryptingCredentials())),
                new SamlAssertionsSignatureValidator(new SamlMessageSignatureValidator(new CredentialFactorySignatureValidator(credentialFactory))),
                new EncryptedResponseFromIdpValidator(new SamlStatusToIdaStatusCodeMapper()),
                new DestinationValidator(URI.create("http://foo.com/bar"), "/bar"),
                new ResponseAssertionsFromIdpValidator(
                        new IdentityProviderAssertionValidator(
                                new IssuerValidator(),
                                new AssertionSubjectValidator(),
                                new AssertionAttributeStatementValidator(),
                                new AssertionSubjectConfirmationValidator()),
                        new MatchingDatasetAssertionValidator(new DuplicateAssertionValidatorImpl(new ConcurrentMapIdExpirationCache<>(new ConcurrentHashMap<>()))),
                        new AuthnStatementAssertionValidator(new DuplicateAssertionValidatorImpl(new ConcurrentMapIdExpirationCache<>(new ConcurrentHashMap<>()))),
                        new IPAddressValidator(),
                        hubEntityId));
    }
}

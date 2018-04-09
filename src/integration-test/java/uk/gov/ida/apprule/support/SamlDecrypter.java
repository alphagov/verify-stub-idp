package uk.gov.ida.apprule.support;

import com.google.common.collect.ImmutableList;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import org.opensaml.saml.saml2.core.Response;
import uk.gov.ida.common.shared.security.PrivateKeyFactory;
import uk.gov.ida.common.shared.security.PublicKeyFactory;
import uk.gov.ida.common.shared.security.X509CertificateFactory;
import uk.gov.ida.saml.core.transformers.AuthnContextFactory;
import uk.gov.ida.saml.core.validators.DestinationValidator;
import uk.gov.ida.saml.core.validators.assertion.AssertionAttributeStatementValidator;
import uk.gov.ida.saml.core.validators.assertion.AuthnStatementAssertionValidator;
import uk.gov.ida.saml.core.validators.assertion.DuplicateAssertionValidator;
import uk.gov.ida.saml.core.validators.assertion.IPAddressValidator;
import uk.gov.ida.saml.core.validators.assertion.IdentityProviderAssertionValidator;
import uk.gov.ida.saml.core.validators.assertion.MatchingDatasetAssertionValidator;
import uk.gov.ida.saml.core.validators.subject.AssertionSubjectValidator;
import uk.gov.ida.saml.core.validators.subjectconfirmation.AssertionSubjectConfirmationValidator;
import uk.gov.ida.saml.deserializers.OpenSamlXMLObjectUnmarshaller;
import uk.gov.ida.saml.deserializers.StringToOpenSamlObjectTransformer;
import uk.gov.ida.saml.deserializers.parser.SamlObjectParser;
import uk.gov.ida.saml.deserializers.validators.Base64StringDecoder;
import uk.gov.ida.saml.deserializers.validators.NotNullSamlStringValidator;
import uk.gov.ida.saml.hub.domain.IdpIdaStatus;
import uk.gov.ida.saml.hub.domain.InboundResponseFromIdp;
import uk.gov.ida.saml.hub.transformers.inbound.IdaResponseFromIdpUnmarshaller;
import uk.gov.ida.saml.hub.transformers.inbound.IdpIdaStatusUnmarshaller;
import uk.gov.ida.saml.hub.transformers.inbound.PassthroughAssertionUnmarshaller;
import uk.gov.ida.saml.hub.transformers.inbound.SamlStatusToIdpIdaStatusMappingsFactory;
import uk.gov.ida.saml.hub.transformers.inbound.decorators.ResponseSizeValidator;
import uk.gov.ida.saml.hub.transformers.inbound.decorators.ValidateSamlResponseIssuedByIdpDestination;
import uk.gov.ida.saml.hub.transformers.inbound.providers.DecoratedSamlResponseToIdaResponseIssuedByIdpTransformer;
import uk.gov.ida.saml.hub.validators.StringSizeValidator;
import uk.gov.ida.saml.hub.validators.response.EncryptedResponseFromIdpValidator;
import uk.gov.ida.saml.hub.validators.response.ResponseAssertionsFromIdpValidator;
import uk.gov.ida.saml.metadata.IdpMetadataPublicKeyStore;
import uk.gov.ida.saml.metadata.JerseyClientMetadataResolver;
import uk.gov.ida.saml.security.AssertionDecrypter;
import uk.gov.ida.saml.security.CredentialFactorySignatureValidator;
import uk.gov.ida.saml.security.DecrypterFactory;
import uk.gov.ida.saml.security.IdaKeyStore;
import uk.gov.ida.saml.security.IdaKeyStoreCredentialRetriever;
import uk.gov.ida.saml.security.SamlAssertionsSignatureValidator;
import uk.gov.ida.saml.security.SamlMessageSignatureValidator;
import uk.gov.ida.saml.security.SigningCredentialFactory;
import uk.gov.ida.saml.security.validators.encryptedelementtype.EncryptionAlgorithmValidator;
import uk.gov.ida.saml.security.validators.issuer.IssuerValidator;
import uk.gov.ida.saml.security.validators.signature.SamlResponseSignatureValidator;
import uk.gov.ida.saml.serializers.XmlObjectToBase64EncodedStringTransformer;

import javax.ws.rs.client.Client;
import java.io.IOException;
import java.net.URI;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static uk.gov.ida.saml.core.test.TestCertificateStrings.HUB_TEST_PRIVATE_ENCRYPTION_KEY;
import static uk.gov.ida.saml.core.test.TestCertificateStrings.HUB_TEST_PUBLIC_ENCRYPTION_CERT;

public class SamlDecrypter {

    private Client client;
    private URI metadataUri;
    private final String hubEntityId;

    public SamlDecrypter(Client client, URI metadataUri, String hubEntityId) {
        this.client = client;
        this.metadataUri = metadataUri;
        this.hubEntityId = hubEntityId;
    }

    public InboundResponseFromIdp decryptSaml(String samlResponse) throws IOException, CertificateException {
        // Manual Guice injection
        StringToOpenSamlObjectTransformer<Response> stringToOpenSamlObjectTransformer = new StringToOpenSamlObjectTransformer(new NotNullSamlStringValidator(),
                new Base64StringDecoder(),
                new ResponseSizeValidator(new StringSizeValidator()),
                new OpenSamlXMLObjectUnmarshaller(new SamlObjectParser()));
        final JerseyClientMetadataResolver jerseyClientMetadataResolver = new JerseyClientMetadataResolver(null,  client, metadataUri);
        try {
            // a parser pool needs to be provided
            BasicParserPool pool = new BasicParserPool();
            pool.initialize();
            jerseyClientMetadataResolver.setParserPool(pool);
            jerseyClientMetadataResolver.setId("SamlDecrypter.MetadataResolver");
            jerseyClientMetadataResolver.initialize();
        } catch (ComponentInitializationException e) {
            e.printStackTrace();
        }
        final SigningCredentialFactory credentialFactory = new SigningCredentialFactory(new AuthnResponseKeyStore(new IdpMetadataPublicKeyStore(jerseyClientMetadataResolver)));
        DecoratedSamlResponseToIdaResponseIssuedByIdpTransformer decoratedSamlResponseToIdaResponseIssuedByIdpTransformer
                = buildDecoratedSamlResponseToIdaResponseIssuedByIdpTransformer(credentialFactory, createHubKeyStore());

        final org.opensaml.saml.saml2.core.Response response = stringToOpenSamlObjectTransformer.apply(samlResponse);
        return decoratedSamlResponseToIdaResponseIssuedByIdpTransformer.apply(response);
    }

    public IdaKeyStore createHubKeyStore() throws IOException {
        PrivateKey privateKey = new PrivateKeyFactory().createPrivateKey(Base64.getDecoder().decode(HUB_TEST_PRIVATE_ENCRYPTION_KEY));

        PublicKey publicKey = new PublicKeyFactory(new X509CertificateFactory()).createPublicKey(HUB_TEST_PUBLIC_ENCRYPTION_CERT);

        List<KeyPair> encryptionKeys = ImmutableList.of(new KeyPair(publicKey, privateKey));
        return new IdaKeyStore(null, encryptionKeys);
    }


    private DecoratedSamlResponseToIdaResponseIssuedByIdpTransformer buildDecoratedSamlResponseToIdaResponseIssuedByIdpTransformer(SigningCredentialFactory credentialFactory, IdaKeyStore keyStore) {
        return new DecoratedSamlResponseToIdaResponseIssuedByIdpTransformer(
                new IdaResponseFromIdpUnmarshaller(
                        new IdpIdaStatusUnmarshaller(new IdpIdaStatus.IdpIdaStatusFactory(), new SamlStatusToIdpIdaStatusMappingsFactory()),
                        new PassthroughAssertionUnmarshaller(new XmlObjectToBase64EncodedStringTransformer<>(), new AuthnContextFactory())),
                new SamlResponseSignatureValidator(new SamlMessageSignatureValidator(new CredentialFactorySignatureValidator(credentialFactory))),
                new AssertionDecrypter(new IdaKeyStoreCredentialRetriever(keyStore), new EncryptionAlgorithmValidator(), new DecrypterFactory()),
                new SamlAssertionsSignatureValidator(new SamlMessageSignatureValidator(new CredentialFactorySignatureValidator(credentialFactory))),
                new EncryptedResponseFromIdpValidator(new SamlStatusToIdpIdaStatusMappingsFactory()),
                new ValidateSamlResponseIssuedByIdpDestination(new DestinationValidator(URI.create("http://foo.com/bar")), "/bar"),
                new ResponseAssertionsFromIdpValidator(
                        new IdentityProviderAssertionValidator(
                                new IssuerValidator(),
                                new AssertionSubjectValidator(),
                                new AssertionAttributeStatementValidator(),
                                new AssertionSubjectConfirmationValidator()),
                        new MatchingDatasetAssertionValidator(new DuplicateAssertionValidator(new ConcurrentHashMap<>())),
                        new AuthnStatementAssertionValidator(new DuplicateAssertionValidator(new ConcurrentHashMap<>())),
                        new IPAddressValidator(),
                        hubEntityId));
    }
}

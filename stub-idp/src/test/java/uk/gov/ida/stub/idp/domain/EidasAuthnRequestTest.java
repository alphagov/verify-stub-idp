package uk.gov.ida.stub.idp.domain;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Extensions;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.impl.ExtensionsBuilder;
import org.opensaml.saml.saml2.core.impl.RequestedAuthnContextBuilder;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA256;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA256;
import org.opensaml.xmlsec.signature.Signature;
import uk.gov.ida.common.shared.security.PrivateKeyFactory;
import uk.gov.ida.common.shared.security.PublicKeyFactory;
import uk.gov.ida.common.shared.security.X509CertificateFactory;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.IdaSamlBootstrap;
import uk.gov.ida.saml.core.extensions.RequestedAttribute;
import uk.gov.ida.saml.core.extensions.SPType;
import uk.gov.ida.saml.core.extensions.impl.RequestedAttributeBuilder;
import uk.gov.ida.saml.core.extensions.impl.RequestedAttributesBuilder;
import uk.gov.ida.saml.core.extensions.impl.RequestedAttributesImpl;
import uk.gov.ida.saml.core.extensions.impl.SPTypeBuilder;
import uk.gov.ida.saml.core.test.OpenSAMLMockitoRunner;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.core.test.TestEntityIds;
import uk.gov.ida.saml.core.test.builders.AuthnContextClassRefBuilder;
import uk.gov.ida.saml.core.test.builders.AuthnRequestBuilder;
import uk.gov.ida.saml.core.test.builders.IssuerBuilder;
import uk.gov.ida.saml.hub.domain.IdaAuthnRequestFromHub;
import uk.gov.ida.saml.hub.domain.LevelOfAssurance;
import uk.gov.ida.saml.security.IdaKeyStore;
import uk.gov.ida.saml.security.IdaKeyStoreCredentialRetriever;
import uk.gov.ida.saml.security.SignatureWithKeyInfoFactory;
import uk.gov.ida.stub.idp.exceptions.InvalidEidasAuthnRequestException;
import uk.gov.ida.stub.idp.repositories.EidasSessionRepository;
import uk.gov.ida.stub.idp.repositories.IdpSessionRepository;
import uk.gov.ida.stub.idp.services.AuthnRequestReceiverService;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(OpenSAMLMockitoRunner.class)
public class EidasAuthnRequestTest {

    @BeforeClass
    public static void doALittleHackToMakeGuicierHappyForSomeReason() {
        JerseyGuiceUtils.reset();
    }

    private static final String SCHEME_ID = "schemeId";

    private static final String SAML_REQUEST = "samlRequest";

    private static final String RELAY_STATE = "relayState";

    private AuthnRequestReceiverService authnRequestReceiverService;

    private AuthnRequest authnRequest;

    @Mock
    private Function<String, IdaAuthnRequestFromHub> samlRequestTransformer;

    @Mock
    private IdpSessionRepository idpSessionRepository;

    @Mock
    private EidasSessionRepository eidasSessionRepository;

    @Mock
    private Function<String, AuthnRequest> stringAuthnRequestTransformer;

    @Before
    public void setUp(){
        IdaSamlBootstrap.bootstrap();
        authnRequestReceiverService = new AuthnRequestReceiverService(
                samlRequestTransformer, idpSessionRepository, eidasSessionRepository, stringAuthnRequestTransformer);
        authnRequest = AuthnRequestBuilder.anAuthnRequest().build();
    }

    @Test
    public void shouldConvertAuthnRequestToEidasAuthnRequest() {
        authnRequest = AuthnRequestBuilder.anAuthnRequest()
                .withIssuer(
                        IssuerBuilder.anIssuer().withIssuerId("issuer-id").build()
                )
                .withId("request-id")
                .withDestination("Destination")
                .build();

        RequestedAuthnContext requestedAuthnContext = new RequestedAuthnContextBuilder().buildObject();

        AuthnContextClassRef authnContextClassRef = AuthnContextClassRefBuilder.anAuthnContextClassRef()
                .withAuthnContextClasRefValue(LevelOfAssurance.SUBSTANTIAL.toString())
                .build();

        requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassRef);
        authnRequest.setRequestedAuthnContext(requestedAuthnContext);

        authnRequest.setExtensions(createEidasExtensions());

        EidasAuthnRequest actualEidasAuthnRequest = EidasAuthnRequest.buildFromAuthnRequest(authnRequest);

        assertThat(actualEidasAuthnRequest.getRequestId()).isEqualTo("request-id");
        assertThat(actualEidasAuthnRequest.getIssuer()).isEqualTo("issuer-id");
        assertThat(actualEidasAuthnRequest.getDestination()).isEqualTo("Destination");
        assertThat(actualEidasAuthnRequest.getRequestedLoa()).isEqualTo("http://eidas.europa.eu/LoA/substantial");

        assertThat(actualEidasAuthnRequest.getAttributes().size()).isEqualTo(1);
        uk.gov.ida.stub.idp.domain.RequestedAttribute requestedAttribute = actualEidasAuthnRequest.getAttributes().get(0);
        assertThat(requestedAttribute.getName()).isEqualTo(IdaConstants.Eidas_Attributes.FamilyName.NAME);
        assertThat(requestedAttribute.isRequired()).isEqualTo(true);
    }

    @Test(expected = InvalidEidasAuthnRequestException.class)
    public void shouldThrowWhenAuthnRequestDoesntContainKeyInfo() {
        when(stringAuthnRequestTransformer.apply(any())).thenReturn(authnRequest);
        authnRequestReceiverService.handleEidasAuthnRequest(SCHEME_ID, SAML_REQUEST, RELAY_STATE, Optional.empty());
    }

    @Test(expected = InvalidEidasAuthnRequestException.class)
    public void shouldThrowWhenAuthnRequestDoesntContainX509Data() {
        authnRequest.setSignature(createSignatureWithKeyInfo());
        authnRequest.getSignature().getKeyInfo().getX509Datas().clear();

        when(stringAuthnRequestTransformer.apply(any())).thenReturn(authnRequest);

        authnRequestReceiverService.handleEidasAuthnRequest(SCHEME_ID, SAML_REQUEST, RELAY_STATE, Optional.empty());
    }

    @Test(expected = InvalidEidasAuthnRequestException.class)
    public void shouldThrowWhenAuthnRequestDoesntContainAnyX509Certs() {
        authnRequest.setSignature(createSignatureWithKeyInfo());
        authnRequest.getSignature().getKeyInfo().getX509Datas().get(0).getX509Certificates().clear();

        when(stringAuthnRequestTransformer.apply(any())).thenReturn(authnRequest);

        authnRequestReceiverService.handleEidasAuthnRequest(SCHEME_ID, SAML_REQUEST, RELAY_STATE, Optional.empty());
    }

    private Signature createSignatureWithKeyInfo() {
        IdaKeyStoreCredentialRetriever keyStoreCredentialRetriever = new IdaKeyStoreCredentialRetriever(createIdaKeyStore());

        SignatureWithKeyInfoFactory keyInfoFactory = new SignatureWithKeyInfoFactory(keyStoreCredentialRetriever,new SignatureRSASHA256(), new DigestSHA256(), "issue-id","signing-cert");

        return keyInfoFactory.createSignature();
    }

    private IdaKeyStore createIdaKeyStore() {
        PublicKeyFactory publicKeyFactory = new PublicKeyFactory(new X509CertificateFactory());

        PrivateKey privateSigningKey = new PrivateKeyFactory().createPrivateKey(Base64.decodeBase64(TestCertificateStrings.PRIVATE_SIGNING_KEYS.get(
                TestEntityIds.HUB_ENTITY_ID)));
        PublicKey publicSigningKey = publicKeyFactory.createPublicKey(TestCertificateStrings.getPrimaryPublicEncryptionCert(TestEntityIds.HUB_ENTITY_ID));

        PrivateKey publicEncryptionKey = new PrivateKeyFactory().createPrivateKey(Base64.decodeBase64(TestCertificateStrings.HUB_TEST_PRIVATE_ENCRYPTION_KEY));;
        PublicKey privateEncryptionKey = publicKeyFactory.createPublicKey(TestCertificateStrings.HUB_TEST_PUBLIC_ENCRYPTION_CERT);

        KeyPair signingKeyPair = new KeyPair(publicSigningKey, privateSigningKey);
        KeyPair encryptionKeyPair = new KeyPair(privateEncryptionKey, publicEncryptionKey);

        return new IdaKeyStore(signingKeyPair, Arrays.asList(encryptionKeyPair));
    }

    private Extensions createEidasExtensions() {
        SPType spType = new SPTypeBuilder().buildObject();
        spType.setValue("public");

        RequestedAttributesImpl requestedAttributes = (RequestedAttributesImpl)new RequestedAttributesBuilder().buildObject();
        requestedAttributes.setRequestedAttributes(createRequestedAttribute(IdaConstants.Eidas_Attributes.FamilyName.NAME));

        Extensions extensions = new ExtensionsBuilder().buildObject();
        extensions.getUnknownXMLObjects().add(spType);
        extensions.getUnknownXMLObjects().add(requestedAttributes);
        return extensions;
    }

    private RequestedAttribute createRequestedAttribute(String requestedAttributeName) {
        RequestedAttribute attr = new RequestedAttributeBuilder().buildObject();
        attr.setName(requestedAttributeName);
        attr.setNameFormat(Attribute.URI_REFERENCE);
        attr.setIsRequired(true);
        return attr;
    }
}

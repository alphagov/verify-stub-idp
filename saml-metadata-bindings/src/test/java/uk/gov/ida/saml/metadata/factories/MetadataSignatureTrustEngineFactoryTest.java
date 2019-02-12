package uk.gov.ida.saml.metadata.factories;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.impl.AuthnRequestBuilder;
import org.opensaml.saml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.SignatureBuilder;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.Signer;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.core.test.TestEntityIds;
import uk.gov.ida.saml.metadata.test.factories.metadata.EntityDescriptorFactory;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.SignatureMethod;
import java.security.KeyException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;

import static org.assertj.core.api.Assertions.assertThat;


public class MetadataSignatureTrustEngineFactoryTest {

    private static DOMMetadataResolver metadataResolver;

    @BeforeClass
    public static void beforeAll() throws Exception {
        InitializationService.initialize();

        //has Hub's entity ID
        EntityDescriptor entityDescriptor = new EntityDescriptorFactory().hubEntityDescriptor();
        Element element = XMLObjectSupport.marshall(entityDescriptor);

        metadataResolver = new DOMMetadataResolver(element);
        metadataResolver.setId("test-metadata-resolver");
        metadataResolver.initialize();
    }

    @Test
    public void shouldSupportValidatingSignaturesUsingKeysInMetadata() throws Exception {
        SignatureTrustEngine signatureTrustEngine = new MetadataSignatureTrustEngineFactory().createSignatureTrustEngine(metadataResolver);

        Signature signature = createSignatureInAuthnRequest(TestEntityIds.HUB_ENTITY_ID);

        CriteriaSet trustBasisCriteria = new CriteriaSet();
        trustBasisCriteria.add(new EntityIdCriterion(TestEntityIds.HUB_ENTITY_ID));
        trustBasisCriteria.add(new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME));

        assertThat(signatureTrustEngine.validate(signature, trustBasisCriteria)).isTrue();
    }

    @Test
    public void shouldSupportInvalidatingSignaturesUsingKeysInMetadata() throws Exception {
        SignatureTrustEngine signatureTrustEngine = new MetadataSignatureTrustEngineFactory().createSignatureTrustEngine(metadataResolver);

        Signature signature = createSignatureInAuthnRequest(TestEntityIds.STUB_IDP_ONE);

        CriteriaSet trustBasisCriteria = new CriteriaSet();
        trustBasisCriteria.add(new EntityIdCriterion(TestEntityIds.STUB_IDP_ONE));
        trustBasisCriteria.add(new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME));

        assertThat(signatureTrustEngine.validate(signature, trustBasisCriteria)).isFalse();
    }

    private Signature createSignatureInAuthnRequest(String id) throws KeyException, SignatureException, MarshallingException {
        Issuer issuer = new IssuerBuilder().buildObject();
        issuer.setValue(id);

        Signature signature = new SignatureBuilder().buildObject();
        String privateKey = TestCertificateStrings.PRIVATE_SIGNING_KEYS.get(TestEntityIds.HUB_ENTITY_ID);

        RSAPrivateKey rsaPrivateKey = KeySupport.buildJavaRSAPrivateKey(privateKey);
        PublicKey publicKey = KeySupport.derivePublicKey(rsaPrivateKey);
        BasicCredential newCredential = new BasicCredential(publicKey, rsaPrivateKey);
        signature.setSigningCredential(newCredential);
        signature.setSignatureAlgorithm(SignatureMethod.RSA_SHA1);
        signature.setCanonicalizationAlgorithm(CanonicalizationMethod.EXCLUSIVE);

        AuthnRequest authnRequest = new AuthnRequestBuilder().buildObject();
        authnRequest.setIssuer(issuer);
        authnRequest.setSignature(signature);
        XMLObjectSupport.marshall(authnRequest);
        Signer.signObject(signature);
        return  signature;
    }

}
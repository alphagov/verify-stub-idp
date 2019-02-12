package uk.gov.ida.saml.security;

import net.shibboleth.utilities.java.support.resolver.Criterion;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.impl.StaticCredentialResolver;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.xmlsec.algorithm.descriptors.DigestMD5;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA1;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA256;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA512;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSAMD5;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA1;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA256;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA512;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import uk.gov.ida.saml.core.test.TestEntityIds;
import uk.gov.ida.saml.security.saml.OpenSAMLMockitoRunner;

import javax.xml.namespace.QName;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.security.saml.builders.ResponseBuilder.aResponse;

@RunWith(OpenSAMLMockitoRunner.class)
public class SignatureValidatorTest {

    private final String issuerId = TestEntityIds.HUB_ENTITY_ID;

    private final SigningCredentialFactory credentialFactory = new SigningCredentialFactory(new HardCodedKeyStore(issuerId));

    private SignatureValidator signatureValidator;

    @Before
    public void setUp() {
        this.signatureValidator = new SignatureValidator() {
            @Override
            protected TrustEngine<Signature> getTrustEngine(String entityId) {
                List<Credential> credentials = credentialFactory.getVerifyingCredentials(entityId);
                CredentialResolver credResolver = new StaticCredentialResolver(credentials);
                KeyInfoCredentialResolver kiResolver = DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver();
                return new ExplicitKeySignatureTrustEngine(credResolver, kiResolver);
            }

            @Override
            protected List<Criterion> getAdditionalCriteria(String entityId, QName role) {
                return Arrays.asList(new Criterion() { });
            }
        };
    }

    @Test
    public void shouldNotAllowMD5Digests() throws SignatureException, SecurityException, MarshallingException {
        final Response signedResponse = aResponse()
                .withDigestAlgorithm(new DigestMD5())
                .build();
        assertThat(signatureValidator.validate(signedResponse, signedResponse.getIssuer().getValue(), SPSSODescriptor.DEFAULT_ELEMENT_NAME)).isFalse();
    }

    @Test
    public void shouldAllowSha1DigestMethod() throws SignatureException, SecurityException, MarshallingException {
        final Response signedResponse = aResponse()
                .withDigestAlgorithm(new DigestSHA1())
                .build();
        assertThat(signatureValidator.validate(signedResponse, signedResponse.getIssuer().getValue(), SPSSODescriptor.DEFAULT_ELEMENT_NAME)).isTrue();
    }

    @Test
    public void shouldAllowSha256DigestMethod() throws SignatureException, SecurityException, MarshallingException {
        final Response signedResponse = aResponse()
                .withDigestAlgorithm(new DigestSHA256())
                .build();
        assertThat(signatureValidator.validate(signedResponse, signedResponse.getIssuer().getValue(), SPSSODescriptor.DEFAULT_ELEMENT_NAME)).isTrue();
    }

    @Test
    public void shouldAllowSha512DigestMethod() throws SignatureException, SecurityException, MarshallingException {
        final Response signedResponse = aResponse()
                .withDigestAlgorithm(new DigestSHA512())
                .build();
        assertThat(signatureValidator.validate(signedResponse, signedResponse.getIssuer().getValue(), SPSSODescriptor.DEFAULT_ELEMENT_NAME)).isTrue();
    }

    @Test
    public void shouldAllowSigningAlgorithmRsaSHA1() throws Exception {
        final Response signedResponse = aResponse()
                .withSignatureAlgorithm(new SignatureRSASHA1())
                .build();
        assertThat(signatureValidator.validate(signedResponse, signedResponse.getIssuer().getValue(), SPSSODescriptor.DEFAULT_ELEMENT_NAME)).isTrue();
    }

    @Test
    public void shouldAllowSigningAlgorithmRsaSHA256() throws Exception {
        final Response signedResponse = aResponse()
                .withSignatureAlgorithm(new SignatureRSASHA256())
                .build();
        assertThat(signatureValidator.validate(signedResponse, signedResponse.getIssuer().getValue(), SPSSODescriptor.DEFAULT_ELEMENT_NAME)).isTrue();
    }

    @Test
    public void shouldAllowSigningAlgorithmRsaSHA512() throws Exception {
        final Response signedResponse = aResponse()
                .withSignatureAlgorithm(new SignatureRSASHA512())
                .build();
        assertThat(signatureValidator.validate(signedResponse, signedResponse.getIssuer().getValue(), SPSSODescriptor.DEFAULT_ELEMENT_NAME)).isTrue();
    }

    @Test
    public void shouldNotAllowSigningAlgorithmMD5() throws Exception {
        final Response signedResponse = aResponse()
                .withSignatureAlgorithm(new SignatureRSAMD5())
                .build();
        assertThat(signatureValidator.validate(signedResponse, signedResponse.getIssuer().getValue(), SPSSODescriptor.DEFAULT_ELEMENT_NAME)).isFalse();
    }
}

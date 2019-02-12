package uk.gov.ida.saml.metadata;

import certificates.values.CACertificates;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ida.common.shared.security.PrivateKeyFactory;
import uk.gov.ida.common.shared.security.X509CertificateFactory;
import uk.gov.ida.eidas.trustanchor.CountryTrustAnchor;
import uk.gov.ida.eidas.trustanchor.Generator;
import uk.gov.ida.saml.core.test.TestCertificateStrings;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EidasTrustAnchorResolverTest {

    @Mock
    private Client client;

    @Mock
    private WebTarget webTarget;

    @Mock
    private Invocation.Builder builder;

    @Mock
    private Response trustAnchorResponse;

    private EidasTrustAnchorResolver eidasTrustAnchorResolver;

    private KeyStore trustStore;
    private PrivateKey privateSigningKey;
    private X509Certificate publicSigningCert;

    private X509CertificateFactory certificateFactory = new X509CertificateFactory();

    @Before
    public void setUp() throws URISyntaxException, KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        URI uri = new URI("https://govukverify-trust-anchor-dev.s3.amazonaws.com/devTrustAnchor");
        privateSigningKey = new PrivateKeyFactory().createPrivateKey(Base64.decodeBase64(TestCertificateStrings.METADATA_SIGNING_A_PRIVATE_KEY));
        publicSigningCert = new X509CertificateFactory().createCertificate(TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT);

        trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        X509Certificate publicSigningCertCA = new X509CertificateFactory().createCertificate(CACertificates.TEST_METADATA_CA);
        trustStore.load(null);
        trustStore.setCertificateEntry("signing_cert_ca", publicSigningCertCA);

        eidasTrustAnchorResolver = new EidasTrustAnchorResolver(uri, client, trustStore);
        when(client.target(uri)).thenReturn(webTarget);
        when(webTarget.request()).thenReturn(builder);
        when(builder.get()).thenReturn(trustAnchorResponse);
    }

    @Test
    public void shouldReturnTrustAnchorsIfResponseIsValid() throws ParseException, SignatureException, JOSEException, CertificateException {
        when(trustAnchorResponse.readEntity(String.class)).thenReturn(createJwsWithACountryTrustAnchor(privateSigningKey));

        List<JWK> result = eidasTrustAnchorResolver.getTrustAnchors();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getKeyID()).isEqualTo("https://eu.entity.id");
    }

    @Test
    public void shouldThrowSignatureExceptionIfResponseIsNotSignedWithExpectedKey() throws ParseException, JOSEException, CertificateEncodingException {
        PrivateKey unexpectedPrivateKey = new PrivateKeyFactory().createPrivateKey(Base64.decodeBase64(TestCertificateStrings.TEST_PRIVATE_KEY));
        when(trustAnchorResponse.readEntity(String.class)).thenReturn(createJwsWithACountryTrustAnchor(unexpectedPrivateKey));

        assertThatThrownBy(() -> eidasTrustAnchorResolver.getTrustAnchors()).isInstanceOf(SignatureException.class);
    }

    @Test
    public void shouldThrowCertificateExceptionIfSigningCertIsNotTrusted() throws ParseException, JOSEException, CertificateEncodingException, KeyStoreException {
        when(trustAnchorResponse.readEntity(String.class)).thenReturn(createJwsWithACountryTrustAnchor(privateSigningKey));

        trustStore.deleteEntry("signing_cert_ca");

        assertThatThrownBy(() -> eidasTrustAnchorResolver.getTrustAnchors()).isInstanceOf(CertificateException.class);
    }

    private String createJwsWithACountryTrustAnchor(PrivateKey privateKey) throws ParseException, JOSEException, CertificateEncodingException {
        Generator generator = new Generator(privateKey, publicSigningCert);
        return generator.generate(Arrays.asList(createJsonAnchor("https://eu.entity.id"))).serialize();
    }

    private String createJsonAnchor(String kid) {
        List<String> certificateChain = asList(
                CACertificates.TEST_ROOT_CA,
                CACertificates.TEST_METADATA_CA,
                TestCertificateStrings.METADATA_SIGNING_B_PUBLIC_CERT
        );

        return createJWK(kid, certificateChain).toJSONString();
    }

    private JWK createJWK(String entityId, List<String> certificates) {
        List<X509Certificate> certs = certificates.stream().map(certificateFactory::createCertificate).collect(Collectors.toList());
        return CountryTrustAnchor.make(certs, entityId);
    }
}

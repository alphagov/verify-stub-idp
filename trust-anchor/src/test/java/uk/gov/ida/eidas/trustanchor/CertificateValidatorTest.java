package uk.gov.ida.eidas.trustanchor;

import com.google.common.collect.ImmutableList;
import com.nimbusds.jose.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CertificateValidatorTest {

    private final PublicKey publicKey = mock(PublicKey.class);
    private final Base64X509CertificateDecoder decoder = mock(Base64X509CertificateDecoder.class);
    private final CertificateValidator testValidator = new CertificateValidator(decoder);

    private List<Base64> x509CertChain;

    @BeforeEach
    public void setup() throws CertificateException {
        Base64 testCert = mock(Base64.class);
        x509CertChain = ImmutableList.of(testCert);

        X509Certificate mockDecodedCert = mock(X509Certificate.class);
        when(decoder.decodeX509(testCert)).thenReturn(mockDecodedCert);
        when(mockDecodedCert.getPublicKey()).thenReturn(publicKey);
    }

    @Test
    public void shouldReturnNoErrorsForValidTrustAnchorCerts() {
        Collection<String> errors = testValidator.checkCertificateValidity(x509CertChain, publicKey);

        assertThat(errors).isEmpty();
    }
}

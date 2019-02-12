package uk.gov.ida.eidas.trustanchor;

import certificates.values.CACertificates;
import com.nimbusds.jose.jwk.JWK;
import org.junit.jupiter.api.Test;
import uk.gov.ida.common.shared.security.X509CertificateFactory;
import uk.gov.ida.saml.core.test.TestCertificateStrings;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class CountryTrustAnchorTest {

    private List<X509Certificate> invalidCertChain = createInvalidCertChain();

    @Test
    public void makeThrowsExceptionIfIncludesInvalidCertificate() {

        JWK trustAnchorKey = null;
        try {
            CountryTrustAnchor.make(invalidCertChain, "key-id");
            fail("CountryTrustAnchor#make should throw exception for invalid certificate");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).startsWith("Managed to generate an invalid anchor: Certificate CN=IDA Stub Country Signing Dev");
        }

        assertThat(trustAnchorKey).isNull();
    }

    @Test
    public void makeValidationCanBeOverRidden() {
        JWK trustAnchorKey = CountryTrustAnchor.make(invalidCertChain, "key-id", false);

        assertThat(trustAnchorKey.getKeyID()).isEqualTo("key-id");
    }

    private List<X509Certificate> createInvalidCertChain() {
        List<String> certificates = asList(
            CACertificates.TEST_ROOT_CA,
            CACertificates.TEST_IDP_CA,
            TestCertificateStrings.STUB_COUNTRY_PUBLIC_NOT_YET_VALID_CERT
        );
        X509CertificateFactory certificateFactory = new X509CertificateFactory();
        return certificates.stream().map(certificateFactory::createCertificate).collect(Collectors.toList());
    }
}

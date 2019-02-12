package uk.gov.ida.eidas.trustanchor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collection;

import static com.nimbusds.jose.JWSAlgorithm.RS256;
import static com.nimbusds.jose.jwk.KeyOperation.VERIFY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CountryTrustAnchorValidatorTest {

    private CertificateValidator mockValidator = mock(CertificateValidator.class);
    private final CountryTrustAnchorValidator testValidator = new CountryTrustAnchorValidator(mockValidator);

    @BeforeEach
    public void setup() {
        when(mockValidator.checkCertificateValidity(any(), any())).thenReturn(ImmutableList.of());
    }

    @Test
    public void validRSATrustAnchorShouldRaiseNoExceptions() {
        RSAKey validTrustAnchor = getValidRSATrustAnchor();
        Collection<String> errors = testValidator.findErrors(validTrustAnchor);

        assertThat(errors).isEmpty();
    }

    @Test
    public void validEC256TrustAnchorShouldRaiseNoExceptions() {
        ECKey validTrustAnchor = getValidECTrustAnchor(Curve.P_256);
        Collection<String> errors = testValidator.findErrors(validTrustAnchor);

        assertThat(errors).isEmpty();
    }

    @Test
    public void validEC384TrustAnchorShouldRaiseNoExceptions() {
        ECKey validTrustAnchor = getValidECTrustAnchor(Curve.P_384);
        Collection<String> errors = testValidator.findErrors(validTrustAnchor);

        assertThat(errors).isEmpty();
    }

    @Test
    public void validEC521TrustAnchorShouldRaiseNoExceptions() {
        ECKey validTrustAnchor = getValidECTrustAnchor(Curve.P_521);
        Collection<String> errors = testValidator.findErrors(validTrustAnchor);

        assertThat(errors).isEmpty();
    }

    private RSAKey getValidRSATrustAnchor() {
        RSAPublicKey mockPublicKey = mock(RSAPublicKey.class);
        BigInteger value = BigInteger.valueOf(2).pow(512);

        when(mockPublicKey.getModulus()).thenReturn(value);
        when(mockPublicKey.getPublicExponent()).thenReturn(BigInteger.valueOf(512));

        return new RSAKey.Builder(mockPublicKey)
                .keyID("TestId")
                .x509CertChain(ImmutableList.of(mock(Base64.class)))
                .algorithm(RS256)
                .keyOperations(ImmutableSet.of(VERIFY))
                .build();
    }

    private ECKey getValidECTrustAnchor(Curve curve) {
        ECPublicKey publicKey = mock(ECPublicKey.class);
        when(publicKey.getW()).thenReturn(curve.toECParameterSpec().getGenerator());
        when(publicKey.getParams()).thenReturn(curve.toECParameterSpec());

        return new ECKey.Builder(curve, publicKey)
                .keyID("TestId")
                .x509CertChain(ImmutableList.of(mock(Base64.class)))
                .algorithm(ECKeyHelper.getJWSAlgorithm(curve))
                .keyOperations(ImmutableSet.of(VERIFY))
                .build();
    }
}

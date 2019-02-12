package uk.gov.ida.eidas.trustanchor;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;

import java.security.interfaces.ECPublicKey;

class ECKeyHelper {

    private static final String P256 = Curve.P_256.getName();
    private static final String P384 = Curve.P_384.getName();
    private static final String P521 = Curve.P_521.getName();

    public static Curve getCurve(ECPublicKey key) {
        return Curve.forECParameterSpec(key.getParams());
    }

    public static JWSAlgorithm getJWSAlgorithm(ECPublicKey key) {
        return getJWSAlgorithm(getCurve(key));
    }

    public static JWSAlgorithm getJWSAlgorithm(Curve curve) {
        final String curveName = curve.getName();

        if (curveName.equals(P256)) {
            return JWSAlgorithm.ES256;
        }

        if (curveName.equals(P384)) {
            return JWSAlgorithm.ES384;
        }

        if (curveName.equals(P521)) {
            return JWSAlgorithm.ES512;
        }

        throw new IllegalArgumentException(String.format(
                "Unsupported curve: %s", curveName
        ));
    }
}

package uk.gov.ida.eidas.trustanchor;

import certificates.values.CACertificates;
import com.google.common.collect.ImmutableList;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import net.minidev.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.ida.common.shared.security.PrivateKeyFactory;
import uk.gov.ida.common.shared.security.X509CertificateFactory;
import uk.gov.ida.saml.core.test.TestCertificateStrings;

import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeneratorTest {

    private Generator generator;

    private RSAPublicKey publicKeyForSigning;

    private X509Certificate certificateForSigning;

    @BeforeEach
    public void setUp() {
        PrivateKey privateKeyForSigning = new PrivateKeyFactory().createPrivateKey(Base64.decodeBase64(TestCertificateStrings.METADATA_SIGNING_A_PRIVATE_KEY));
        certificateForSigning = new X509CertificateFactory().createCertificate(TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT);
        generator = new Generator(privateKeyForSigning, certificateForSigning);
        publicKeyForSigning = (RSAPublicKey) certificateForSigning.getPublicKey();
    }

    @Test
    public void shouldHandleZeroInput() throws ParseException, JOSEException, CertificateEncodingException {
        List<String> files = new ArrayList<>();

        JWSObject output = generator.generate(files);

        assertSigned(output, publicKeyForSigning);
        assertTrue(output.getPayload().toJSONObject().containsKey("keys"));
        assertTrue(((List<Object>)output.getPayload().toJSONObject().get("keys")).isEmpty());
    }

    @Test
    public void shouldHandleOneString() throws JOSEException, CertificateEncodingException {
        String countryPublicCert = CACertificates.TEST_ROOT_CA;
        X509Certificate countryCertificate = new X509CertificateFactory().createCertificate(countryPublicCert);
        HashMap<String, List<X509Certificate>> trustAnchorMap = new HashMap<>();
        trustAnchorMap.put("https://generator.test", ImmutableList.of(countryCertificate));
        JWSObject output = generator.generateFromMap(trustAnchorMap);

        assertSigned(output, publicKeyForSigning);
        assertTrue(output.getPayload().toJSONObject().containsKey("keys"));

        List<JSONObject> keys = (List<JSONObject>) output.getPayload().toJSONObject().get("keys");
        assertEquals(1, keys.size());
        assertEquals("https://generator.test",keys.get(0).getAsString("kid"));
        assertArrayEquals(certificateForSigning.getEncoded(), output.getHeader().getX509CertChain().get(0).decode());
    }

    @Test
    public void shouldHandleChainOfCertificates() throws CertificateEncodingException, JOSEException {
        List<String> certificates = asList(
            CACertificates.TEST_ROOT_CA,
            CACertificates.TEST_IDP_CA,
            TestCertificateStrings.STUB_COUNTRY_PUBLIC_PRIMARY_CERT
        );
        X509CertificateFactory factory = new X509CertificateFactory();
        List<X509Certificate> certs = certificates.stream().map(factory::createCertificate).collect(Collectors.toList());
        HashMap<String, List<X509Certificate>> trustAnchorMap = new HashMap<>();
        trustAnchorMap.put("https://generator.test", certs);
        JWSObject output = generator.generateFromMap(trustAnchorMap);

        assertSigned(output, publicKeyForSigning);
        assertTrue(output.getPayload().toJSONObject().containsKey("keys"));

        List<JSONObject> keys = (List<JSONObject>) output.getPayload().toJSONObject().get("keys");
        assertEquals(1, keys.size());
        assertEquals("https://generator.test",keys.get(0).getAsString("kid"));
    }

    @Test
    public void shouldThrowOnMissingValue() {
        List<String> valueList = Arrays.asList("kty", "key_ops", "kid", "alg", "e", "n", "x5c");

        for (String attribute : valueList) {
            JSONObject invalid = createJsonObject();
            invalid.remove(attribute);
            assertThrows(ParseException.class, () -> generator.generate(Collections.singletonList(invalid.toJSONString())));
        }
    }

    @Test
    public void shouldThrowOnIncorrectValue() {
        Map<String, Object> incorrectValues = new HashMap<>();
        incorrectValues.put("alg","A128KW");
        incorrectValues.put("kty", "oct");

        for (String attribute: incorrectValues.keySet()) {
            JSONObject jsonObject = createJsonObject();
            jsonObject.replace(attribute, incorrectValues.get(attribute));
            assertThrows(ParseException.class, () -> generator.generate(Collections.singletonList(jsonObject.toJSONString())));
        }
    }

    @Test
    public void shouldThrowOnIncorrectKeyopsValues() {
        List<Object> incorrectValues = Arrays.asList(Arrays.asList(), Arrays.asList("sign"), Arrays.asList("verify", "sign"), "verify");

        for (Object attribute: incorrectValues) {
            JSONObject jsonObject = createJsonObject();
            jsonObject.replace("key_ops", attribute);
            assertThrows(ParseException.class, () -> generator.generate(Collections.singletonList(jsonObject.toJSONString())));
        }
    }

    @Test
    public void shouldThrowWhenCertificateDoesNotMatchKeyParameters() {
        JSONObject jsonObject = createJsonObject();
        jsonObject.replace("x5c", Collections.singletonList(TestCertificateStrings.TEST_PUBLIC_CERT));

        assertThrows(ParseException.class, () -> generator.generate(Collections.singletonList(jsonObject.toJSONString())));
    }

    @Test
    public void shouldCheckAllX509Certificates(){
        JSONObject jsonObject = createJsonObject();
        jsonObject.replace("x5c", Arrays.asList(TestCertificateStrings.UNCHAINED_PUBLIC_CERT, TestCertificateStrings.TEST_PUBLIC_CERT));

        assertThrows(ParseException.class, () -> generator.generate(Collections.singletonList(jsonObject.toJSONString())));
    }

    @Test
    public void shouldThrowWhenDateOfX509CertificateIsInvalid(){

        String expiredCert = TestCertificateStrings.STUB_COUNTRY_PUBLIC_EXPIRED_CERT;

        X509Certificate x509Certificate = new X509CertificateFactory().createCertificate(expiredCert);
        RSAPublicKey rsaPublicKey = (RSAPublicKey) x509Certificate.getPublicKey();

        JSONObject jsonObject = createJsonObject();
        jsonObject.replace("x5c", Collections.singletonList(expiredCert));
        jsonObject.replace("e", new String (Base64.encodeInteger(rsaPublicKey.getPublicExponent())));
        jsonObject.replace("n", new String (Base64.encodeInteger(rsaPublicKey.getModulus())));

        assertThatExceptionOfType(ParseException.class)
                .isThrownBy(() -> generator.generate(Collections.singletonList(jsonObject.toJSONString())))
                .withMessageContaining("X.509 certificate has expired");
    }

    @Test
    public void shouldThrowOnOneInvalidKey() {
        List<String> files = new ArrayList<>();
        files.add(createJsonObject("https://1.generator.test").toJSONString());
        JSONObject invalidObject = createJsonObject("https://2.generator.test");
        invalidObject.remove("kid");
        files.add(invalidObject.toJSONString());

        assertThrows(ParseException.class, () -> generator.generate(files));
    }

    @Test
    public void shouldHandleMultipleStrings() throws ParseException, JOSEException, CertificateEncodingException {
        List<String> files = new ArrayList<>();
        for (int i = 0; i < 1024; i++) {
            files.add(createJsonObject(String.format("https://%d.generator.test", i)).toJSONString());
        }
        JWSObject output = generator.generate(files);

        assertSigned(output, publicKeyForSigning);
        assertTrue(output.getPayload().toJSONObject().containsKey("keys"));

        List<JSONObject> keys = (List<JSONObject>) output.getPayload().toJSONObject().get("keys");
        Set<String> kidSet = keys.stream().map(x -> x.getAsString("kid")).collect(Collectors.toSet());

        assertEquals(1024, kidSet.size());
        for (int i = 0; i < 1024; i++) {
            assertTrue(kidSet.contains(String.format("https://%d.generator.test", i)));
        }
    }

    private JSONObject createJsonObject() {
        return createJsonObject("https://generator.test");
    }

    private JSONObject createJsonObject(String kid) {
        String countryPublicCert = CACertificates.TEST_ROOT_CA;
        countryPublicCert = countryPublicCert.replace("-----BEGIN CERTIFICATE-----\n", "").replace("\n-----END CERTIFICATE-----", ""
        ).trim();
        X509Certificate x509Certificate = new X509CertificateFactory().createCertificate(countryPublicCert);
        RSAPublicKey rsaPublicKey = (RSAPublicKey) x509Certificate.getPublicKey();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("kty", "RSA");
        jsonObject.put("key_ops", Collections.singletonList("verify"));
        jsonObject.put("kid", kid);
        jsonObject.put("alg", "RS256");
        jsonObject.put("e", new String (Base64.encodeInteger(rsaPublicKey.getPublicExponent())));
        jsonObject.put("n", new String (Base64.encodeInteger(rsaPublicKey.getModulus())));
        jsonObject.put("x5c", Collections.singletonList(countryPublicCert));

        return jsonObject;
    }

    private void assertSigned(JWSObject output, RSAPublicKey signedKey) throws JOSEException {
        assertEquals(JWSObject.State.SIGNED, output.getState());
        assertNotNull(output.getSignature());
        assertNotEquals("", output.getSignature().decodeToString());
        assertTrue(output.verify(new RSASSAVerifier(signedKey)));
    }
}

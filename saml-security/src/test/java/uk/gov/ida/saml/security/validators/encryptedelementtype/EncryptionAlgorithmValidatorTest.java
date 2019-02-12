package uk.gov.ida.saml.security.validators.encryptedelementtype;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.w3c.dom.Element;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;
import uk.gov.ida.saml.security.errors.SamlTransformationErrorFactory;
import uk.gov.ida.saml.security.saml.OpenSAMLMockitoRunner;
import uk.gov.ida.saml.security.saml.SamlTransformationErrorManagerTestHelper;
import uk.gov.ida.saml.security.saml.deserializers.XmlUtils;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;

@RunWith(OpenSAMLMockitoRunner.class)
public class EncryptionAlgorithmValidatorTest {

    private EncryptionAlgorithmValidator validator;

    @Before
    public void setup() {
        validator = new EncryptionAlgorithmValidator();
    }

    @Test
    public void validateShouldNotThrowSamlExceptionIfEncryptionAlgorithmIsAES128() {
        final String encryptionAlgorithm = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
        assertThatCode(() -> validator.validate(createStandardEncryptedAssertion(encryptionAlgorithm, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP, true))).doesNotThrowAnyException();
        assertThatCode(() -> validator.validate(createOtherTypeOfEncryptedAssertion(encryptionAlgorithm, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP))).doesNotThrowAnyException();
    }

    @Test
    public void validateShouldNotThrowSamlExceptionIfEncryptionAlgorithmIsWhitelisted() {
        final String algoIdBlockcipherAes256 = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256;
        final Set<String> whitelistedAlgos = ImmutableSet.of(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, algoIdBlockcipherAes256);
        final Set<String> whitelistedKeyTransportAlgos = ImmutableSet.of(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);

        validator = new EncryptionAlgorithmValidator(whitelistedAlgos, whitelistedKeyTransportAlgos);
        assertThatCode(() -> validator.validate(createStandardEncryptedAssertion(algoIdBlockcipherAes256, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP, true))).doesNotThrowAnyException();
        assertThatCode(() -> validator.validate(createOtherTypeOfEncryptedAssertion(algoIdBlockcipherAes256, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP))).doesNotThrowAnyException();
    }

    @Test
    public void validateShouldThrowSamlExceptionIfEncryptionAlgorithmIsNotAES128() throws Exception {
        final String encryptionAlgorithm = EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES;
        EncryptedAssertion standardEncryptedAssertion = createStandardEncryptedAssertion(encryptionAlgorithm, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP, true);

        assertSamlValidationFailure(
                SamlTransformationErrorFactory.unsupportedEncryptionAlgortithm(encryptionAlgorithm),
                standardEncryptedAssertion);
    }

    @Test
    public void validateShouldNotThrowIfKeyTransportAlgorithmIsInWhitelist() throws Exception {
        final Set<String> whitelistedAlgos = ImmutableSet.of(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
        final Set<String> whiteListedKeyTransportAlgos = ImmutableSet.of(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);

        validator = new EncryptionAlgorithmValidator(whitelistedAlgos, whiteListedKeyTransportAlgos);
        assertThatCode(() -> validator.validate(createStandardEncryptedAssertion(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15, true))).doesNotThrowAnyException();
        assertThatCode(() -> validator.validate(createOtherTypeOfEncryptedAssertion(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15))).doesNotThrowAnyException();
    }

    @Test
    public void validateShouldThrowSamlExceptionIfKeyMissing() throws Exception {
        final String keyTransportAlgorithm = EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP;
        EncryptedAssertion assertionWithNoKey =  createStandardEncryptedAssertion(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, keyTransportAlgorithm, false);

        assertSamlValidationFailure(
                SamlTransformationErrorFactory.unableToLocateEncryptedKey(),
                assertionWithNoKey);
    }


    @Test
    public void validateShouldNotThrowSamlExceptionIfKeyTransportAlgorithmIsRSAOAEP() throws Exception {
        final String keyTransportAlgorithm = EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP;
        assertThatCode(() -> validator.validate(createStandardEncryptedAssertion(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, keyTransportAlgorithm, true))).doesNotThrowAnyException();
    }

    @Test
    public void validateShouldThrowSamlExceptionIfKeyTransportAlgorithmIsNotRSAOAEP() throws Exception {
        final String keyTransportAlgorithm = EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15;
        EncryptedAssertion standardEncryptedAssertion = createStandardEncryptedAssertion(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, keyTransportAlgorithm, true);

        assertSamlValidationFailure(
                SamlTransformationErrorFactory.unsupportedKeyEncryptionAlgorithm(keyTransportAlgorithm),
                standardEncryptedAssertion);
    }

    private EncryptedAssertion createStandardEncryptedAssertion(
            String encryptionAlgorithm,
            String keyTransportAlgorithm, boolean includeKey) throws Exception {

        String ea = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<saml2:EncryptedAssertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">\n" +
                "    <xenc:EncryptedData xmlns:xenc=\"http://www.w3.org/2001/04/xmlenc#\" Id=\"_ea3527f5f42ce12803baf66d6f59e4fc\"\n" +
                "                        Type=\"http://www.w3.org/2001/04/xmlenc#Element\">\n" +
                "        <xenc:EncryptionMethod Algorithm=\"" + encryptionAlgorithm + "\"\n" +
                "                               xmlns:xenc=\"http://www.w3.org/2001/04/xmlenc#\"/>\n" +
                "        <ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\n" +
                "            <ds:RetrievalMethod Type=\"http://www.w3.org/2001/04/xmlenc#EncryptedKey\"\n" +
                "                                URI=\"#_5f845451a8a7271c8de4c0a1163364dd\"/>\n" +
                "        </ds:KeyInfo>\n" +
                "        <xenc:CipherData xmlns:xenc=\"http://www.w3.org/2001/04/xmlenc#\">\n" +
                "            <xenc:CipherValue>\n" +
                "                +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\n" +
                "            </xenc:CipherValue>\n" +
                "        </xenc:CipherData>\n" +
                "    </xenc:EncryptedData>\n";

        if(includeKey)
            ea +=
                "    <xenc:EncryptedKey xmlns:xenc=\"http://www.w3.org/2001/04/xmlenc#\" Id=\"_5f845451a8a7271c8de4c0a1163364dd\">\n" +
                "        <xenc:EncryptionMethod Algorithm=\"" + keyTransportAlgorithm + "\"\n" +
                "                               xmlns:xenc=\"http://www.w3.org/2001/04/xmlenc#\">\n" +
                "            <ds:DigestMethod xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"\n" +
                "                             Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/>\n" +
                "        </xenc:EncryptionMethod>\n" +
                "        <xenc:CipherData xmlns:xenc=\"http://www.w3.org/2001/04/xmlenc#\">\n" +
                "            <xenc:CipherValue>\n" +
                "                lhw6mIkk2J6R9oNhvd6M3fuVKCJWaerVMOgB/otaHQhKvVO6vuREPvYoxjyTa8I+Bx+8KdezCLqnpUrm57zAQE6GtvYdL0HUApLB+3G4IWb+QERIynmM13ZQCoVI0zsvI6ETgsnWQ5bsXSKxGXvCQdvvYtAlIs4kCI+9P2O6DQE=\n" +
                "            </xenc:CipherValue>\n" +
                "        </xenc:CipherData>\n" +
                "        <xenc:ReferenceList>\n" +
                "            <xenc:DataReference URI=\"#_ea3527f5f42ce12803baf66d6f59e4fc\" />\n" +
                "        </xenc:ReferenceList>\n" +
                "    </xenc:EncryptedKey>\n";
        ea +=
                "</saml2:EncryptedAssertion>";

        final Element element = XmlUtils.convertToElement(ea);
        return (EncryptedAssertion) XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(element).unmarshall(element);
    }

    private EncryptedAssertion createOtherTypeOfEncryptedAssertion(
            String encryptionAlgorithm,
            String keyTransportAlgorithm) throws Exception {

        String ea = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<saml2:EncryptedAssertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">\n" +
                "        <xenc:EncryptedData xmlns:xenc=\"http://www.w3.org/2001/04/xmlenc#\"\n" +
                "                            xmlns:dsig=\"http://www.w3.org/2000/09/xmldsig#\"\n" +
                "                            Type=\"http://www.w3.org/2001/04/xmlenc#Element\"\n" +
                "                            >\n" +
                "            <xenc:EncryptionMethod Algorithm=\"" + encryptionAlgorithm + "\" />\n" +
                "            <dsig:KeyInfo xmlns:dsig=\"http://www.w3.org/2000/09/xmldsig#\">\n" +
                "                <xenc:EncryptedKey>\n" +
                "                    <xenc:EncryptionMethod Algorithm=\"" + keyTransportAlgorithm + "\" />\n" +
                "                    <xenc:CipherData>\n" +
                "                        <xenc:CipherValue>gVz+lMovIwa46n1QuJPe5L36OVoysRGWXR7ToXgsakf12ejcKXQ0mGdcNp+e2wg2Ti/BmHPpsMhbv+F6ngRLsLUaPbkDsh96iw5CE4i94NSIULntarafDBE4Dryo+GqpFHWzLq9j2hM0h8jqnPVD3Gd9LR58mdCgt1EYpRkpCvc=</xenc:CipherValue>\n" +
                "                    </xenc:CipherData>\n" +
                "                </xenc:EncryptedKey>\n" +
                "            </dsig:KeyInfo>\n" +
                "            <xenc:CipherData>\n" +
                "                <xenc:CipherValue>48Ie2f4DIlG4GZ39GNwb59BOyaR/D4qFAQy5kQ5UsSPdyUZCGP+aoIWWuc7uHFbYHOjiyx6Xnz8Tace4sS0kr2yZnlLTkaT9dl1z6GGxpBrbW7b3wPC8RUbiad/kd5nVckln8JcNJlKO5T4Hj4gYB5NZeDJQIdGB9mOvRLP052HuLPbRejIYdixiPJsWG5em25O2p0enfVSmuaB+/sBNfr4Y6WejBK1MZoq//T1z9zl9O2xD6vnb14B1e6hurGMLWIWek5wUzXayV6QRc6RICxiDm1BgUiPb9vvBbdQv85hSWNQUSxcXiULnts1Fo22r6Z6QMwet4TY09HFJW9O83t3v0JbM2jqgi89yzXgofiia1fB8hy4N5bAa1vqjfHhKcFSqynfLTfat59q/68KP3prXbPDEcLPYQ7chz9FSla4rNe8g1GNa3EkrIY/laWa2RQ2TtG21wg1YipGeMGt+0yAC/tK4ex8TBc46kXk635TAlVWWyRgm2cr9el+uQH1RVvqbRyV1Zsc2z1zfnJjIiHSKX/lC4bectTuDyJrvhmeLwqPd6U87H56z8ZKiVkjLJGMsTbU7aYWtNDAkoII2aqJdHnLzpdOSwYNQw0ZI0/QU6mbx7Z2xkgE76nwhYcIOqAvI3xP5/o4b6oNXjeLGo7L080tqf1t+GbtkCe1ViN/S2zXk7aA2Ka0/+cO8j/CtelPKmPWSzzN9LHad6JwD05x7xcZSC+9SoNP1cy94YxY2znSE6VZjQfDoUo0YHHJWML3b2cAqp0hLKc4wjuEPABvBeqTcSE6+ZEFKyVQd/Mzv5Lo5MeYeJCeKxalM8u9nOoBOeZvJMChLglnFklxAyVaOi97drluGlD0OAuhuKm7TQXKfqpXFt3BxkNrL7iJGfThSVQ1X79oUfsvQBHE1YF06k3LjgDklHQIjI57bh+9W+6VlRCyR4epJahyeRxN5wkaWdcGiWVk5YfcbundSZCFuvpxmr4cRqZ0o45M5dfgmyVsSUW7UxFCCX4Iu/U2TARkz6spvCuO1Yp15W1QClqqybEPuHIOs2H2AO7Z863Vuwa8N058AoOwKL2hkA53kC/2IET4ytC/CtbC/zRaEEgeCwyvSFfsjnimXVzII6YyE7OZRKtoG/8mKNPxjr66ChlSlgGRi+1P5f/Y+O1nV4Tetuz2DN3rYXPbTtwUL1qhoD2PyIEKcR78v1D6XiyRtJVkhrh/WL+VyTQ6LXGN/dobLf57PPZiF0SzW2jbc26jmMUBSdfdkn8ZCtiMARexhpMFj2sNGyWgZ5S3YyTvcS0r7f/KjbBEQ1k9W+GHep8Q2EpHPjM8/vhMwQPRtJSS8se8oOi6PQfB6l/o3pME3YNHIkuli/qg9Vv+DylWxmEEb6XwPf74+Uc/FfExU/tln3jcBhyU2dMcKr62iAX1yzVsB/OmXhjR0cEL8Skneq7Gj3LhlFLjyC7ZYu5xyVYU7l7hHRnvB4LecM7GBlPZe+utbTnRv3DSbF6lFmeXlleG/nX1aanSw0QR4Vjaje4JXFlFlrXmYgUXVHdlOTQ4TPNnJiNkdgs7elzz82zZMiDpODEn5bKxlXfRqiYToI5ZKFJw8Ujso8vbFPy9MHqEJf+zf8/aa7Ztffoc0kXv3gnqq7L4sDs2vwYNlxZP86nHY81bSQN2/CPo27VBYIwudbbq38IIs3sflSwAx7o/+G3KSgPO3XM4gnhY6JKpswHrXRPy8iIsCqysLG/yeslhC4c81WFQOr3gYCVXYuieIiljSGbd02pW0jHAz8UJPEO9nQriNbVXLPSwwldOAlA0f6NEP5cDXUF4nwng/9oTtuzszczJvq2Rmy/99TfK3faELLVYJKYQvs/PgIl87+Kbc/XCPuQ9ZI5r6RAPqjbc4HfzK3x369N+jrpXFBZBrRWxChSGKOGyCjTfZjBgYxd3gnQgJRA5WvVxZsIuo5QbGziN3OfjwBJA8+TEemUiPHUMnaxkMmDyCxmpVnVgYb9Zp3FCkBIVRcMx5Xz2U8LeR3H1qsG05K2PWoS3UGfPOCQS/PgV0xWW2pupSRNHREsihhNlsRY6ruXFCDBYxJDdzVVpy1QhARcTb7Gxkrhd5xVRrycUKoHyEI+vB+e0OAKM+LiCgffZ+L/vTg+ct1dI/EsVB8LUMu/HV1JrVFbTe0Xkkzz4vpU9zMGmfRNHuZyZV+uSTwfUGdT6MotW5XRzo0H0qcHCyRl4OShsRRgssrFoOriGHczdm7GhDFBMfgzW1HpOi0SWH2vB+4IeorWE3QNGJvCz2XsSyttb1cROH/U1qrXfwI2K2F5krbrAoSB3t+TJL/fm5V5WVYvp4y6/C5hEXIiuDFJYUsg7n4fPBc7UcioV82zv2+NRSqK/9JvvnwnhlP0oAvoYjTFg1SDsYePDlFAmaIbopQlLbcB7OT+nvRKVgE0cJ+r21yILty+j/qU05C6yiUIRaPfQhrRDXpAV3Kezg9x3rFNPkKaIRNL1iJ2VJZlyezQV1+Fn7NRdv9PCYZbJjF/t/lnlRqtiDbx87OJCFx2edxp1x6yajU2SScwdrCxNZyXMWi6iNMIUijINuRFL+yCkVhpERRsqQup+sClBvgnKlDQFqSS9svBSO/m0Eg3o5hnyVZAkViEllTPFAbzPM1ud8ivjm1lapOCgTqq5G2OKZpsm0Epvol6SRRzWApK0b3tlmioXpZ+RCeQtVMRVAtAgLU2wd8VDCIw2SrTzncTkrRS3CBtX5SSOKylmcGFnt9v4OPNoSc4MgVlGNiOoyJwkC/GGiR1TjZ0VoH/Ls1oq3YrcfaTHmg5GcfgGZfuGOemSaEfVK8L2EgBZAdZCmgJQqZAWN7lo1zTaIkY/KaHfRI/0OUvhjexgFLEIJiwT2fqPHf4NYTlXa/KvpB6ztrRxdlShSiZI9o898pKBNxWl2cxftqm+zF79+ARm++ekkZrXBpyEqA//t3LV6nbwh4yoqXzNZoQgDptXVjCrV1vDDzXPGB0NzYed1UBYXI0yMHmPzUdaqH/h1QoXGdd+iZukwxKJBcAIDe0DhDXRA+k8WcJ+KblpJ/6+Yvt+o47/rcz4Nf5VvApQrBVwra4Y/+ql/9I3NRPFM8Bs7uWZf/MCrBXHR/9QgM0q+p5QLtaZBuyONjjXmwNSCIqphmCpaB2LwpONKgoiIDfq4TmsAf4N+Xz+CNYLeQlCRUQvMLZOB9WVX4etaNgeUmk+FUqJX5RgI68MqDzNuDj9QgBAzHTUfifUymtf/4GIpKYUk1n/6fEF8AyEnnMlnGjCzlsRCDBoECeZmbGiUWo9Pt337kf4qQF1Z91+9E1VjUy+kTUu7teGcoLXvUx3dQnVWUXYwgVrbKcFa+KIw902fKif82CUFjbGQVgugfd7970e7AFJvZoAtEh0DbhnweSZ9iZTabDDYSVpSQu/Ebd74AkalLfKfMnInGHNQ5L9yl9xnCrbCg2A46+gBrraNKv2yV8Vg/2ww9GTBuNC7kmlNeDIkUdNoq93E5Sq2EKsqaEk7uD0TnJgmlJn++naSOKQ0UIj+wwPuz4uWDqIqUa7JXQN249He3tLc8NR01xfYOP2AziM8EmsoLCHuNEjJIlZPYpL4zEfTKACVzcxY0AH1PezJur0IEwWOn2dh1g0UyAWMZ/RpytKYntd0YrbGjrdQaXGRelaAg0hfwbDw+HReO+nRx1uYNnqcN4sdoXGC3f/H+SuVLfNCtopLRQO+8TLit2BnH178aBxwYdS2HWgfZTJojLgRH4JZ+2wdPxDHbd9uTOEvO+7W68ezwj+QEWX4m4AOaUSBH6g3R3Yk5SpuxMLNZoBiCLoOEYsEgZn5w4gDRfRCwy49uW97KbT9XJbl0uiEb39WXktMOtvQ0Cih/17fOFStlayjTshM2c5Gi/3dBKsXe27g2u2nUSv0B3D2vdhfNtdAwj+mhfG+lbPyTv1MqOHRamKvmLxRZJA9skOHbE4WA7yRNrkhQS/fvLve5IVtDjlFulC9NyDGRqznsnBCiDD4866DY25EdpJTaUgEmQzpqYBLLR5D3GqKV6L4VyWPIi9uTNyC5QNXqhR/O6tx3Z+2yHiKV7S3YuquTvo1rr3kEPUDBTy2+BKrp2pXBv5o7mRZx1WefmXIKN8bWQykTRt21jBlG02snwv0IHk2i/KmdhF6E4/rtg9nL3s7L7Ou70jT+FVZdUOD+lK7qRZGmgWaRehdsiFns/+os+Nhgpn7LLlBuZqjJ6JC0BWMKHJaylxyum59XkNkgjPYUqzdbSBVculZfJT6v4B2nSWt46R6kAzRBakoarmcls6LJ3qoO47L32EIpq85PBVfsX2YzBsNGHLVgYYjcKFkloRJhwUq498zyopMLIrkzzWl3BQfPu8fDUKKpNCkwjaFEEMkm1EqelX5S9RHcOTx0L6VV3tua5CDYj/cdu4pFwpNQsTG0Bt+/I5iCy9HVZLzuDCr+KPXXIzPY52eCljyI8CmnCY1u8egnsCdQIDDFk1/ze4TaAbezn/G/v0Fe339qYT+e2XBx6iwxGyF5llBpd4nhVnjAMIBvaLa9X4ACa+HwqqFPFcAnCOYiTHB4Zec1OMNzFW7ltzToghpdeSk7OYj4k42QvV3MdMymMOoa9zv8JM7VjYFkRZGlEHrIxqE6PJ1rUxiMBki02rbp4mQ43cANPJSEFv/BQFJwarxoJfb19RcTwi5KJ4mZLQn9XuiIMu9+vPSDu7NReyrx+hStrMs1AGaI2mK1hDRrQt3SBN0raPAMA6kXjcPNmiC1oviZCZmugIAYK5gc9N7daSs72Diyx9Iw2wfyrLNt+IphRmyicXNU0L7Y5SOiZs1BD8rar7cj4D8m7Kz53/ZgteWZ14XkQgNWwb2LoRb298O8e/TRUf4s1JM7QtZA3zUOO40mxaTUQZIfjI0oTHj1iVqLNxp297V+wMBSAzV3RtXnYnMbtGPjHYJLO2wGn3Of/jN9Kgb7k65AK+rXVyps/hp2pf+zodQfqDO6KKjGz9nf2GPPJQ/XBK5sd5/CFtIJLDsghxkMf+qciko4vyZEicA8F7NTk/0I5mQEFa6OP/8Ye7BQKK4Jq/Qi0Kbf5/E5MxT7gJN2yEYoFLDNWgXFghlj/hwg2wYHjXhX5Ce+O8xrqGG5wzj2hwcOQ8YSolxo92+/h1so1jVBZFBJOyOR9wZPecX21qVZ0zi1LwDKCUh00wJWURJkqWBZBsPfokw3Vrw/KORBTqstSpgqmT2OuVZY9APtg/wB+A8gggIoidCUCI+C5/bze/jElMA7Xuqn/f23OHexkVzsKrWyVeycEW5GeKWnQZucSloYSTHgFXCd85ewZ5lsCPKPaXf3OQ8xXbX2D7e5jkjkz4YHZFStxK+F9I44zTzmvO9U8BdNEVELER/lqoaeYsdeY+uSBC9sEbuI4MMBhdmn2m16g7hLViMTPP+SuDGuOumspSKmBofn0YH8BvavQGqc6HznBW2lg9dwpyT2+fScUasVHxHFal4i536X/6E/3F7ehkpwRWGrf5dQmDj7lLMiGYEnRrSkOsw79/5vEloINyLzuBlTt05UUwsWrBzqOYN7XSWjiyUMYCGnmfBtYu14EUAP5+SzTDy2BJVVrhrCWjbqQecj6DS</xenc:CipherValue>\n" +
                "            </xenc:CipherData>\n" +
                "        </xenc:EncryptedData>\n" +
                "    </saml2:EncryptedAssertion>";
        final Element element = XmlUtils.convertToElement(ea);
        return (EncryptedAssertion) XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(element).unmarshall(element);
    }

    private void assertSamlValidationFailure(SamlValidationSpecificationFailure failure, final EncryptedAssertion assertion) {
        SamlTransformationErrorManagerTestHelper.validateFail(
                () -> validator.validate(assertion),
                failure
        );
    }
}

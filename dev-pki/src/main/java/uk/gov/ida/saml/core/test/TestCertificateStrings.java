package uk.gov.ida.saml.core.test;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public final class TestCertificateStrings {

    private TestCertificateStrings() {
    }

    private static String createInlineCertificate(String pemString) {
        String BEGIN = "-----BEGIN CERTIFICATE-----\n";
        String END = "\n-----END CERTIFICATE-----";
        return pemString.substring(pemString.lastIndexOf(BEGIN) + BEGIN.length(), pemString.indexOf(END));
    }

    private static String readPrivateKey(String name){
        try {
            URL resource = Resources.getResource("dev-keys/" + name);
            return Base64.getEncoder().encodeToString(Resources.toByteArray(resource));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public static final String TEST_ENTITY_ID = "a-test-entity";
    public static final Map<String, String> PUBLIC_SIGNING_CERTS;
    public static final Map<String, List<String>> PUBLIC_ENCRYPTION_CERTS;
    public static final Map<String, String> PRIVATE_SIGNING_KEYS;
    public static final Map<String, List<String>> PRIVATE_ENCRYPTION_KEYS;

    // stub-idp signing
    public static final String TEST_PRIVATE_KEY = readPrivateKey("stub_idp_signing_primary.pk8");

    // stub-idp signing
    public static final String TEST_PUBLIC_CERT = createInlineCertificate(PemCertificateStrings.STUB_IDP_PUBLIC_SIGNING_CERT);

    public static final String EXPIRED_SIGNING_PUBLIC_CERT = createInlineCertificate(PemCertificateStrings.EXPIRED_SIGNING_PUBLIC_CERT);

    public static final String EXPIRED_SELF_SIGNED_SIGNING_PUBLIC_CERT = createInlineCertificate(PemCertificateStrings.EXPIRED_SELF_SIGNED_SIGNING_PUBLIC_CERT);

    public static final String EXPIRED_SIGNING_PRIVATE_KEY = readPrivateKey("expired_signing.pk8");


    public static final String HUB_TEST_PRIVATE_ENCRYPTION_KEY = readPrivateKey("hub_encryption_primary.pk8");

    public static final String HUB_TEST_PRIVATE_SIGNING_KEY = readPrivateKey("hub_signing_primary.pk8");

    public static final String HUB_CONNECTOR_TEST_PRIVATE_ENCRYPTION_KEY = readPrivateKey("hub_connector_encryption_primary.pk8");

    public static final String HUB_CONNECTOR_TEST_PRIVATE_SIGNING_KEY = readPrivateKey("hub_connector_signing_primary.pk8");

    // TODO generate a new one of these
    public final static String HUB_TEST_PRIVATE_SECONDARY_SIGNING_KEY = readPrivateKey("hub_signing_secondary.pk8");

    public static final String HUB_TEST_PUBLIC_ENCRYPTION_CERT = createInlineCertificate(PemCertificateStrings.HUB_TEST_PUBLIC_ENCRYPTION_CERT);

    public static final String HUB_TEST_PUBLIC_SIGNING_CERT =  createInlineCertificate(PemCertificateStrings.HUB_TEST_PUBLIC_SIGNING_CERT);

    public static final String HUB_CONNECTOR_TEST_PUBLIC_ENCRYPTION_CERT = createInlineCertificate(PemCertificateStrings.HUB_CONNECTOR_TEST_PUBLIC_ENCRYPTION_CERT);

    public static final String HUB_CONNECTOR_TEST_PUBLIC_SIGNING_CERT =  createInlineCertificate(PemCertificateStrings.HUB_CONNECTOR_TEST_PUBLIC_SIGNING_CERT);

    // TODO generate a new one of these
    public static final String HUB_TEST_SECONDARY_PUBLIC_SIGNING_CERT = createInlineCertificate(PemCertificateStrings.HUB_TEST_SECONDARY_PUBLIC_SIGNING_CERT);

    public static final String TEST_RP_PRIVATE_ENCRYPTION_KEY = readPrivateKey("sample_rp_encryption_primary.pk8");

    public static final String TEST_RP_PRIVATE_SIGNING_KEY = readPrivateKey("sample_rp_signing_primary.pk8");

    public static final String TEST_RP_PUBLIC_ENCRYPTION_CERT = createInlineCertificate(PemCertificateStrings.SAMPLE_RP_PUBLIC_ENCRYPTION_CERT);

    public static final String TEST_RP_PUBLIC_SIGNING_CERT = createInlineCertificate(PemCertificateStrings.SAMPLE_RP_PUBLIC_SIGNING_CERT);

    public static final String TEST_RP_MS_PRIVATE_ENCRYPTION_KEY = readPrivateKey("sample_rp_msa_encryption_primary.pk8");

    public static final String TEST_RP_MS_PRIVATE_SIGNING_KEY = readPrivateKey("sample_rp_msa_signing_primary.pk8");

    public static final String TEST_RP_MS_PUBLIC_ENCRYPTION_CERT = createInlineCertificate(PemCertificateStrings.SAMPLE_RP_MS_PUBLIC_ENCRYPTION_CERT);

    public static final String TEST_RP_MS_PUBLIC_SIGNING_CERT = createInlineCertificate(PemCertificateStrings.SAMPLE_RP_MS_PUBLIC_SIGNING_CERT);

    public static final String HEADLESS_RP_PUBLIC_SIGNING_CERT = TEST_RP_PUBLIC_SIGNING_CERT;

    public static final String HEADLESS_RP_PRIVATE_SIGNING_KEY = TEST_RP_PRIVATE_SIGNING_KEY;

    public static final String HEADLESS_RP_MS_PUBLIC_SIGNING_CERT = TEST_RP_MS_PUBLIC_SIGNING_CERT;

    public static final String HEADLESS_RP_MS_PRIVATE_SIGNING_KEY = TEST_RP_MS_PRIVATE_SIGNING_KEY;

    public static final String HEADLESS_RP_MS_PRIVATE_ENCRYPTION_KEY = TEST_RP_MS_PRIVATE_ENCRYPTION_KEY;

    public static final String HEADLESS_RP_PUBLIC_ENCRYPTION_CERT = TEST_RP_PUBLIC_ENCRYPTION_CERT;

    public static final String HEADLESS_RP_MS_PUBLIC_ENCRYPTION_CERT = TEST_RP_MS_PUBLIC_ENCRYPTION_CERT;

    public static final String STUB_IDP_PUBLIC_PRIMARY_PRIVATE_KEY = readPrivateKey("stub_idp_signing_primary.pk8");

    public static final String STUB_IDP_PUBLIC_PRIMARY_CERT = createInlineCertificate(PemCertificateStrings.STUB_IDP_PUBLIC_SIGNING_CERT);

    public static final String STUB_IDP_PUBLIC_SECONDARY_PRIVATE_KEY = readPrivateKey("stub_idp_signing_secondary.pk8");

    public static final String STUB_IDP_PUBLIC_SECONDARY_CERT = createInlineCertificate(PemCertificateStrings.STUB_IDP_PUBLIC_SIGNING_SECONDARY_CERT);

    public static final String STUB_COUNTRY_PUBLIC_PRIMARY_PRIVATE_KEY = readPrivateKey("stub_country_signing_primary.pk8");

    public static final String STUB_COUNTRY_PUBLIC_PRIMARY_CERT = createInlineCertificate(PemCertificateStrings.STUB_COUNTRY_PUBLIC_SIGNING_CERT);

    public static final String STUB_COUNTRY_PUBLIC_SECONDARY_PRIVATE_KEY = readPrivateKey("stub_country_signing_secondary.pk8");

    public static final String STUB_COUNTRY_PUBLIC_SECONDARY_CERT = createInlineCertificate(PemCertificateStrings.STUB_COUNTRY_PUBLIC_SIGNING_SECONDARY_CERT);

    public static final String STUB_COUNTRY_PUBLIC_TERTIARY_PRIVATE_KEY = readPrivateKey("stub_country_signing_tertiary.pk8");

    public static final String STUB_COUNTRY_PUBLIC_TERTIARY_CERT = createInlineCertificate(PemCertificateStrings.STUB_COUNTRY_PUBLIC_SIGNING_TERTIARY_CERT);

    public static final String STUB_COUNTRY_PUBLIC_EXPIRED_PRIVATE_KEY = readPrivateKey("stub_country_signing_expired.pk8");

    public static final String STUB_COUNTRY_PUBLIC_EXPIRED_CERT = createInlineCertificate(PemCertificateStrings.STUB_COUNTRY_PUBLIC_SIGNING_EXPIRED_CERT);

    public static final String STUB_COUNTRY_PUBLIC_NOT_YET_VALID_PRIVATE_KEY = readPrivateKey("stub_country_signing_not_yet_valid.pk8");

    public static final String STUB_COUNTRY_PUBLIC_NOT_YET_VALID_CERT = createInlineCertificate(PemCertificateStrings.STUB_COUNTRY_PUBLIC_SIGNING_NOT_YET_VALID_CERT);



    public static final String UNCHAINED_PRIVATE_KEY =
            "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALGtlH968fIt6cYokQ8YWcvcNVfENKrkE3//yBSXI2IbUj6UVQpCZjD0bMShat4DPC+yYSw4mXo7XxvfkRiYLxzfWgKq8xL/4wjpBC457kmr2G8qujm7egoRxIS+SCQQtU/vQqXGRlXnlIZAuIZpGxlfJuQSz0pBQaZh4W4WBhGDAgMBAAECgYEAiYmPePsHzOtjmiQO3fuAj0D//deA2YRB4AR0shOorSnvCUgzaASsLFsY00EMg51Herh/ZgbOL4NEBUSTgdFULaACPQlAcPZc/BkKlP6n1Xz74KdtFKW8kxd0SoVxlL1WfHKP2KCs0IYMRtsWZdfcnF2ybu1sd0dHqL74BREo++kCQQDdRh39IDyD0QgQllmbpWTHsoU5BTAPVohYEx+9e2lTnkP2rhYIg3rTIQfaa0dRB4AO74nsyX7rDPtr1bziqj7VAkEAzY/1owrdby1v8rDx9LJ4cZeICnSIEje3vXC7e0qPqNArKAx1dTgt8x3mciTaDqcoz69Srks/OXzssKf+ZuKq9wJBAK9C61vj3aq2tYGV5NHgdeuqndTlJATyEDpao2hMyMc/cyt/BdqmcXGrFvJMyIcIvsiVuJRBoPKCLN5jxCFwoSUCQD2CKAQDSkLsG6VI4P1RMcz7hI9sUxLwbSBYTSEVLGtc7qzrHXJXvxgSCFR7RmxABGwwj9LrXR28ja5Gdk8e3/0CQB7pVgitNX6wSx5zB3fOasg7wgpOBsvQCPqlqYLG3+b9mPgZOg2vz97NeQJJQ3Ro0y9fHHo7Y0BCwGiPcBYVeU4=+XXr82WshZuMBs14ab96yzsKutbqNppLMl8uG/BLuHYkeuFRXcTiWr2x+aUKe2DYj6867asb4lOmRrA4HWulZ7ginvUZZk+6h3fQxxyIrv7rEvGPEmUjnAgMBAAECgYB21O0Bj3yt+6mitK8lVvAer8E3uZpg4do0L2ULU5Cs3wasnvjgHL4PmN31IpGUnuzE7B4n3Yvso9nlTve2utXZ8F1FMRohGOaov9QdIFmko7fHJryo7L6piW6RJsE06tfsCFXyfCrzVM/mqtCqbKRETA8p82rpjUpbit66gg1twQJBAPW1CCXM/TmrJbrqQsoig3HL5JFXa/BT0WskWm34FJbTqqwBEBTMr0ZTLouc5yBNDoXc2cLcVIcZKXLQeDMUMtkCQQDxoqb4p40Y/RBdhDwG7aL1hyRIKGBiKZgqP24KhoVHxGz43UFyIgloz9rpmjfI2qyh6LkTXdnerpyfwZ0xKIG/AkBmbSkQWPvW5nm8CZv+F5UkAxMtY3wjm01ZlLX90cUDewlS2r6RbSJD9rkYijfvRzAerYo5qQ+zodGdgzoYUPtBAkEAgrgZWB9cZ45P0id9Sco5BFatvBOLwM77sK9L2onXhHGw+hjIFPRw3rDDnuh9ET2rcpOxu9ZjzOHtfUeCwFtHhQJAFwBFPDIfejC+6Wids24B1uD8xqWF8y6EEDH7zH2RIj4xtJW+pVG1l3XVpvXMPkId9NTIdHDwpolqg+/cb1WKzA==";

    public static final String UNCHAINED_PUBLIC_CERT =
            "MIICsDCCAhmgAwIBAgIJANxvwSnbFJp1MA0GCSqGSIb3DQEBBQUAMEUxCzAJBgNV\n" +
                    "BAYTAkFVMRMwEQYDVQQIEwpTb21lLVN0YXRlMSEwHwYDVQQKExhJbnRlcm5ldCBX\n" +
                    "aWRnaXRzIFB0eSBMdGQwHhcNMTIxMTE0MTY1ODQyWhcNMTIxMjE0MTY1ODQyWjBF\n" +
                    "MQswCQYDVQQGEwJBVTETMBEGA1UECBMKU29tZS1TdGF0ZTEhMB8GA1UEChMYSW50\n" +
                    "ZXJuZXQgV2lkZ2l0cyBQdHkgTHRkMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKB\n" +
                    "gQCxrZR/evHyLenGKJEPGFnL3DVXxDSq5BN//8gUlyNiG1I+lFUKQmYw9GzEoWre\n" +
                    "AzwvsmEsOJl6O18b35EYmC8c31oCqvMS/+MI6QQuOe5Jq9hvKro5u3oKEcSEvkgk\n" +
                    "ELVP70KlxkZV55SGQLiGaRsZXybkEs9KQUGmYeFuFgYRgwIDAQABo4GnMIGkMB0G\n" +
                    "A1UdDgQWBBSOu1RJmPHRe/fbGBv0hZzoyMk1QDB1BgNVHSMEbjBsgBSOu1RJmPHR\n" +
                    "e/fbGBv0hZzoyMk1QKFJpEcwRTELMAkGA1UEBhMCQVUxEzARBgNVBAgTClNvbWUt\n" +
                    "U3RhdGUxITAfBgNVBAoTGEludGVybmV0IFdpZGdpdHMgUHR5IEx0ZIIJANxvwSnb\n" +
                    "FJp1MAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEFBQADgYEAFpCJ7hF1r9zqnGbQ\n" +
                    "p33GQ0dCfuYYEXxGDkfN0aoxY2ubOk2D403Lrv091LvfYc8gd/R510AKIm3psVOC\n" +
                    "R1+/X1IUXNGKfqVMWC/QMqXIOG60SCXirbTZS77Ssye6hm8HENZZ4SQNIR+mHxHr\n" +
                    "8aMNpVcUBnhmehV84MCNZNXRuWU=";

    public static final String METADATA_SIGNING_A_PRIVATE_KEY = readPrivateKey("metadata_signing_a.pk8");
    public static final String METADATA_SIGNING_A_PUBLIC_CERT = createInlineCertificate(PemCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT);
    public static final String METADATA_SIGNING_B_PRIVATE_KEY = readPrivateKey("metadata_signing_b.pk8");
    public static final String METADATA_SIGNING_B_PUBLIC_CERT = createInlineCertificate(PemCertificateStrings.METADATA_SIGNING_B_PUBLIC_CERT);

    static {
        PUBLIC_SIGNING_CERTS = ImmutableMap.<String, String>builder()
            .put(TEST_ENTITY_ID, TEST_PUBLIC_CERT)
            .put(TestEntityIds.HUB_ENTITY_ID, HUB_TEST_PUBLIC_SIGNING_CERT)
            .put(TestEntityIds.HUB_SECONDARY_ENTITY_ID, HUB_TEST_SECONDARY_PUBLIC_SIGNING_CERT)
            .put(TestEntityIds.HUB_CONNECTOR_ENTITY_ID, HUB_CONNECTOR_TEST_PUBLIC_SIGNING_CERT)
            .put(TestEntityIds.TEST_RP, TEST_RP_PUBLIC_SIGNING_CERT)
            .put(TestEntityIds.TEST_RP_MS, TEST_RP_MS_PUBLIC_SIGNING_CERT)
            .put(TestEntityIds.HEADLESS_RP, HEADLESS_RP_PUBLIC_SIGNING_CERT)
            .put(TestEntityIds.HEADLESS_RP_MS, HEADLESS_RP_MS_PUBLIC_SIGNING_CERT)
            .put(TestEntityIds.STUB_IDP_ONE, STUB_IDP_PUBLIC_PRIMARY_CERT)
            .put(TestEntityIds.STUB_IDP_TWO, STUB_IDP_PUBLIC_PRIMARY_CERT)
            .put(TestEntityIds.STUB_IDP_THREE, STUB_IDP_PUBLIC_PRIMARY_CERT)
            .put(TestEntityIds.STUB_IDP_FOUR, STUB_IDP_PUBLIC_PRIMARY_CERT)
            .put(TestEntityIds.STUB_COUNTRY_ONE, STUB_COUNTRY_PUBLIC_PRIMARY_CERT)
            .put(TestEntityIds.STUB_COUNTRY_TWO, STUB_COUNTRY_PUBLIC_PRIMARY_CERT)
            .build();
    }

    static {
        PRIVATE_SIGNING_KEYS = ImmutableMap.<String, String>builder()
            .put(TEST_ENTITY_ID, TEST_PRIVATE_KEY)
            .put(TestEntityIds.HUB_ENTITY_ID, HUB_TEST_PRIVATE_SIGNING_KEY)
            .put(TestEntityIds.HUB_SECONDARY_ENTITY_ID, HUB_TEST_PRIVATE_SECONDARY_SIGNING_KEY)
            .put(TestEntityIds.HUB_CONNECTOR_ENTITY_ID, HUB_CONNECTOR_TEST_PRIVATE_SIGNING_KEY)
            .put(TestEntityIds.TEST_RP, TEST_RP_PRIVATE_SIGNING_KEY)
            .put(TestEntityIds.TEST_RP_MS, TEST_RP_MS_PRIVATE_SIGNING_KEY)
            .put(TestEntityIds.HEADLESS_RP, HEADLESS_RP_PRIVATE_SIGNING_KEY)
            .put(TestEntityIds.HEADLESS_RP_MS, HEADLESS_RP_MS_PRIVATE_SIGNING_KEY)
            .put(TestEntityIds.STUB_IDP_ONE, STUB_IDP_PUBLIC_PRIMARY_PRIVATE_KEY)
            .put(TestEntityIds.STUB_IDP_TWO, STUB_IDP_PUBLIC_PRIMARY_PRIVATE_KEY)
            .put(TestEntityIds.STUB_IDP_THREE, STUB_IDP_PUBLIC_PRIMARY_PRIVATE_KEY)
            .put(TestEntityIds.STUB_IDP_FOUR, STUB_IDP_PUBLIC_PRIMARY_PRIVATE_KEY)
            .put(TestEntityIds.STUB_COUNTRY_ONE, STUB_COUNTRY_PUBLIC_PRIMARY_PRIVATE_KEY)
            .put(TestEntityIds.STUB_COUNTRY_TWO, STUB_COUNTRY_PUBLIC_PRIMARY_PRIVATE_KEY)
            .build();
    }

    static {
        PUBLIC_ENCRYPTION_CERTS = ImmutableMap.<String, List<String>>builder()
            .put(TEST_ENTITY_ID, ImmutableList.of(TEST_PUBLIC_CERT))
            .put(TestEntityIds.HUB_ENTITY_ID, ImmutableList.of(HUB_TEST_PUBLIC_ENCRYPTION_CERT, TEST_PUBLIC_CERT))
            .put(TestEntityIds.HUB_CONNECTOR_ENTITY_ID, ImmutableList.of(HUB_CONNECTOR_TEST_PUBLIC_ENCRYPTION_CERT))
            .put(TestEntityIds.TEST_RP, ImmutableList.of(TEST_RP_PUBLIC_ENCRYPTION_CERT))
            .put(TestEntityIds.TEST_RP_MS, ImmutableList.of(TEST_RP_MS_PUBLIC_ENCRYPTION_CERT))
            .put(TestEntityIds.HEADLESS_RP, ImmutableList.of(HEADLESS_RP_PUBLIC_ENCRYPTION_CERT))
            .put(TestEntityIds.HEADLESS_RP_MS, ImmutableList.of(HEADLESS_RP_MS_PUBLIC_ENCRYPTION_CERT))
            .build();
    }

    static {
        PRIVATE_ENCRYPTION_KEYS = ImmutableMap.<String, List<String>>builder()
            .put(TEST_ENTITY_ID, ImmutableList.of(TEST_PRIVATE_KEY))
            .put(TestEntityIds.HUB_ENTITY_ID, ImmutableList.of(HUB_TEST_PRIVATE_ENCRYPTION_KEY, TEST_PRIVATE_KEY))
            .put(TestEntityIds.HUB_SECONDARY_ENTITY_ID, ImmutableList.of(HUB_TEST_PRIVATE_ENCRYPTION_KEY, TEST_PRIVATE_KEY))
            .put(TestEntityIds.HUB_CONNECTOR_ENTITY_ID, ImmutableList.of(HUB_CONNECTOR_TEST_PRIVATE_ENCRYPTION_KEY))
            .put(TestEntityIds.TEST_RP, ImmutableList.of(TEST_RP_PRIVATE_ENCRYPTION_KEY))
            .put(TestEntityIds.TEST_RP_MS, ImmutableList.of(TEST_RP_MS_PRIVATE_ENCRYPTION_KEY))
            .put(TestEntityIds.HEADLESS_RP, ImmutableList.of(HEADLESS_RP_MS_PRIVATE_ENCRYPTION_KEY))
            .put(TestEntityIds.HEADLESS_RP_MS, ImmutableList.of(HEADLESS_RP_MS_PRIVATE_ENCRYPTION_KEY))
            .build();
    }

    public static String getPrimaryPublicEncryptionCert(final String issuerId) {
        return PUBLIC_ENCRYPTION_CERTS.get(issuerId).get(0);
    }

    public static String getSecondaryPublicEncryptionCert(final String issuerId) {
        List<String> allCerts = PUBLIC_ENCRYPTION_CERTS.get(issuerId);
        if(allCerts.size() < 2){
            throw new RuntimeException("No secondary public encryption cert for : " + issuerId);
        }
        return allCerts.get(1);
    }
}

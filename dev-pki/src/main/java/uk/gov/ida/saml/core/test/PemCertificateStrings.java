package uk.gov.ida.saml.core.test;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

public abstract class PemCertificateStrings {


    private static String readFile(String name) {
        try {
            URL resource = Resources.getResource("dev-keys/" + name);
            return Resources.toString(resource, Charset.defaultCharset());
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public static final String EXPIRED_SIGNING_PUBLIC_CERT = readFile("expired_signing.crt");

    public static final String EXPIRED_SELF_SIGNED_SIGNING_PUBLIC_CERT = readFile("expired_self_signed_signing.crt");

    public static final String HUB_TEST_PUBLIC_ENCRYPTION_CERT = readFile("hub_encryption_primary.crt");

    public static final String HUB_TEST_PUBLIC_SIGNING_CERT =  readFile("hub_signing_primary.crt");

    public static final String HUB_CONNECTOR_TEST_PUBLIC_ENCRYPTION_CERT = readFile("hub_connector_encryption_primary.crt");

    public static final String HUB_CONNECTOR_TEST_PUBLIC_SIGNING_CERT =  readFile("hub_connector_signing_primary.crt");

    public static final String HUB_TEST_SECONDARY_PUBLIC_SIGNING_CERT = readFile("hub_signing_secondary.crt");

    public static final String SAMPLE_RP_PUBLIC_ENCRYPTION_CERT = readFile("sample_rp_encryption_primary.crt");

    public static final String SAMPLE_RP_PUBLIC_SIGNING_CERT = readFile("sample_rp_signing_primary.crt");

    public static final String SAMPLE_RP_MS_PUBLIC_ENCRYPTION_CERT = readFile("sample_rp_msa_encryption_primary.crt");

    public static final String SAMPLE_RP_MS_PUBLIC_SIGNING_CERT = readFile("sample_rp_msa_signing_primary.crt");

    public static final String STUB_IDP_PUBLIC_SIGNING_CERT = readFile("stub_idp_signing_primary.crt");

    public static final String STUB_IDP_PUBLIC_SIGNING_SECONDARY_CERT = readFile("stub_idp_signing_secondary.crt");

    public static final String STUB_COUNTRY_PUBLIC_SIGNING_CERT = readFile("stub_country_signing_primary.crt");

    public static final String STUB_COUNTRY_PUBLIC_SIGNING_SECONDARY_CERT = readFile("stub_country_signing_secondary.crt");

    public static final String STUB_COUNTRY_PUBLIC_SIGNING_TERTIARY_CERT = readFile("stub_country_signing_tertiary.crt");

    public static final String STUB_COUNTRY_PUBLIC_SIGNING_EXPIRED_CERT = readFile("stub_country_signing_expired.crt");

    public static final String STUB_COUNTRY_PUBLIC_SIGNING_NOT_YET_VALID_CERT = readFile("stub_country_signing_not_yet_valid.crt");

    public static final String METADATA_SIGNING_A_PUBLIC_CERT = readFile("metadata_signing_a.crt");

    public static final String METADATA_SIGNING_B_PUBLIC_CERT = readFile("metadata_signing_b.crt");
}

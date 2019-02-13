package certificates.values;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;

public class CACertificates {
    private static String readCertificateFile(String name) {
        try {
            return IOUtils.toString(CACertificates.class.getResourceAsStream("/ca-certificates/" + name));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public static final String TEST_ROOT_CA = readCertificateFile("ida-root-ca.pem.test");

    public static final String TEST_METADATA_CA = readCertificateFile("ida-metadata-ca.pem.test");

    public static final String TEST_IDP_CA = readCertificateFile("ida-intermediary-ca.pem.test");

    public static final String TEST_RP_CA = readCertificateFile("ida-intermediary-rp-ca.pem.test");

    public static final String TEST_CORE_CA = readCertificateFile("idap-core-ca.pem.test");
}

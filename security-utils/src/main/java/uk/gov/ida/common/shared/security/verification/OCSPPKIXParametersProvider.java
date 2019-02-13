package uk.gov.ida.common.shared.security.verification;

import java.security.KeyStore;
import java.security.Security;
import java.security.cert.PKIXParameters;

public class OCSPPKIXParametersProvider extends PKIXParametersProvider {
    @Override
    public PKIXParameters getPkixParameters(KeyStore keyStore) {
        PKIXParameters pkixParameters = super.getPkixParameters(keyStore);
        pkixParameters.setRevocationEnabled(true);
        Security.setProperty("ocsp.enable", "true");
        return pkixParameters;
    }
}

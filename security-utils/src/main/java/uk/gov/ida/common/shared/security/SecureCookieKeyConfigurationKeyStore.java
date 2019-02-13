package uk.gov.ida.common.shared.security;

import com.google.common.base.Throwables;
import uk.gov.ida.common.shared.configuration.KeyConfiguration;
import uk.gov.ida.common.shared.configuration.SecureCookieKeyConfiguration;
import uk.gov.ida.common.shared.configuration.SecureCookieKeyStore;

import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class SecureCookieKeyConfigurationKeyStore implements SecureCookieKeyStore {

    private KeyConfiguration keyConfiguration;

    @Inject
    public SecureCookieKeyConfigurationKeyStore(@SecureCookieKeyConfiguration KeyConfiguration keyConfiguration) {
        this.keyConfiguration = keyConfiguration;
    }

    @Override
    public Key getKey() {
        String keyUri = keyConfiguration.getKeyUri();
        try {
            return getSecureCookieKey(keyUri);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw Throwables.propagate(e);
        }
    }

    private static Key getSecureCookieKey(String keyUri) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try(InputStream inputStream = new FileInputStream(new File(keyUri))) {
            byte[] ous = FileUtils.readStream(inputStream);
            return new SecretKeySpec(ous, "HmacSHA1");
        }
    }
}

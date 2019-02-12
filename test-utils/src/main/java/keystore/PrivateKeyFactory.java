package keystore;

import com.google.common.base.Throwables;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

class PrivateKeyFactory {

    public PrivateKey createPrivateKey(byte[] cert) {
        KeySpec keySpec = new PKCS8EncodedKeySpec(cert);
        KeyFactory keyFactory;

        try {
            keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw Throwables.propagate(e);
        }

    }
}

package keystore;

import org.junit.rules.ExternalResource;

import java.io.File;
import java.security.KeyStore;

public class KeyStoreRule extends ExternalResource {
    private final KeyStoreResource keyStoreResource;

    public KeyStore getKeyStore() {
        return keyStoreResource.getKeyStore();
    }

    @Override
    protected void before() throws Throwable {
        keyStoreResource.create();
    }

    @Override
    protected void after() {
        keyStoreResource.delete();
    }

    public String getAbsolutePath() {
        return keyStoreResource.getAbsolutePath();
    }

    public String getPassword() {
        return keyStoreResource.getPassword();
    }

    public File getFile() {
        return keyStoreResource.getFile();
    }

    public KeyStoreRule(KeyStoreResource keyStoreResource) {
        this.keyStoreResource = keyStoreResource;
    }
}

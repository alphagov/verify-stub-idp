package uk.gov.ida.eidas.utils.keyloader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Partially cribbed from net.shibboleth.tool.xmlsectool.CredentialHelper
 */
public class PKCS11KeyLoader {
  private final Class<? extends Provider> provider;
  private final File pkcs11Config;
  private final String keyPassword;

  public PKCS11KeyLoader(final Class<? extends Provider> provider, final File pkcs11Config, final String keyPassword) {
    this.provider = provider;
    this.pkcs11Config = pkcs11Config;
    this.keyPassword = keyPassword;
  }

  public PrivateKey getSigningKey(String keyAlias) throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException,
          NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, CertificateException {
    final KeyStore keyStore = getKeyStore(this.pkcs11Config, this.provider);
    keyStore.load(null, keyPassword.toCharArray());
    final PrivateKeyEntry keyEntry = (PrivateKeyEntry)keyStore.getEntry(keyAlias,
            new KeyStore.PasswordProtection(keyPassword.toCharArray()));
    if (keyEntry == null) {
      throw new RuntimeException("Key store contains wrong kind of credential, need Private key");
    }
    PrivateKey privateKey = keyEntry.getPrivateKey();
    if (privateKey == null) {
      throw new RuntimeException("Key store didn't contain Private Key");
    }
    return privateKey;
  }

  public X509Certificate getPublicCertificate(String alias) throws CertificateException, NoSuchAlgorithmException, IOException, InvocationTargetException,
          NoSuchMethodException, InstantiationException, KeyStoreException, IllegalAccessException {
    final KeyStore keyStore = getKeyStore(this.pkcs11Config, this.provider);
    keyStore.load(null, keyPassword.toCharArray());

    Certificate certificate = keyStore.getCertificate(alias);
    if (certificate.equals(null)) {
      throw new RuntimeException(("Certificate not found with alias: " + alias));
    }
    if (!(certificate instanceof X509Certificate)) {
      throw new RuntimeException("Certificate is not an X509Certificate");
    }
    return (X509Certificate) certificate;
  }

  private static KeyStore getKeyStore(File pkcs11Config, Class<? extends Provider> klazz) throws NoSuchMethodException, SecurityException, InstantiationException,
          IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, KeyStoreException {
    final Constructor<? extends Provider> constructor = klazz.getConstructor(String.class);
    final Provider provider = constructor.newInstance(pkcs11Config.getAbsolutePath());
    provider.load(new FileReader(pkcs11Config));
    Security.addProvider(provider);
    return KeyStore.getInstance("PKCS11", provider);
  }
}

package uk.gov.ida.eidas.utils.keyloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileKeyLoader {

  private static final String RSA_ALGORITHM = "RSA";
  private static final String CERTIFICATE_TYPE = "X.509";
  private static final String ECDSA_ALGORITHM = "ECDSA";
  private static final String BOUNCY_CASTLE_PROVIDER = "BC";

  public static RSAPrivateKey loadRSAKey(final File keyFile) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
    final KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
    return (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Files.readAllBytes(keyFile.toPath())));
  }

  public static ECPrivateKey loadECKey(final File keyFile) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
    final KeyFactory keyFactory = KeyFactory.getInstance(ECDSA_ALGORITHM, BOUNCY_CASTLE_PROVIDER);
    return (ECPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Files.readAllBytes(keyFile.toPath())));
  }

  public static X509Certificate loadCert(final File certFile) {
    try {
      CertificateFactory certificateFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE);
      return (X509Certificate) certificateFactory.generateCertificate(new FileInputStream(certFile));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static List<X509Certificate> loadCerts(final File[] certFiles) {
    return Arrays.stream(certFiles)
            .map(FileKeyLoader::loadCert)
            .collect(Collectors.toList());
  }
}

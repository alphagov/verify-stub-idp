package uk.gov.ida.saml.security;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyName;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.xmlsec.signature.X509Data;

public class SignatureWithKeyInfoFactory extends SignatureFactory {

    private String issuerId;
    private String signingCertificate;

    public SignatureWithKeyInfoFactory(IdaKeyStoreCredentialRetriever keyStoreCredentialRetriever, SignatureAlgorithm signatureAlgorithm, DigestAlgorithm digestAlgorithm, String issuerId, String signingCertificate) {
        super(keyStoreCredentialRetriever, signatureAlgorithm, digestAlgorithm);
        this.issuerId = issuerId;
        this.signingCertificate = signingCertificate;
    }

    @Override
    public Signature createSignature() {
        Signature signature = super.createSignature();
        signature.setKeyInfo(createKeyInfo(issuerId, signingCertificate));
        return signature;
    }

    private X509Certificate createX509Certificate(String cert) {
        X509Certificate x509Certificate = (X509Certificate) XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(X509Certificate.DEFAULT_ELEMENT_NAME).buildObject(X509Certificate.DEFAULT_ELEMENT_NAME);
        x509Certificate.setValue(cert);
        return x509Certificate;
    }

    private X509Data createX509Data() {
        return (X509Data) XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(X509Data.DEFAULT_ELEMENT_NAME).buildObject(X509Data.DEFAULT_ELEMENT_NAME, X509Data.TYPE_NAME);
    }

    private KeyInfo createKeyInfo(String keyNameValue) {
        final KeyInfo keyInfo = (KeyInfo) XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(KeyInfo.DEFAULT_ELEMENT_NAME).buildObject(KeyInfo.DEFAULT_ELEMENT_NAME, KeyInfo.TYPE_NAME);
        if (keyNameValue != null) {
            KeyName keyName = createKeyName(keyNameValue);
            keyInfo.getKeyNames().add(keyName);
        }
        return keyInfo;
    }

    private KeyInfo createKeyInfo(final String issuerId, final String certificateValue) {
        KeyInfo keyInfo = createKeyInfo(issuerId);
        X509Data x509Data = createX509Data();
        final X509Certificate x509Certificate = createX509Certificate(certificateValue);
        x509Data.getX509Certificates().add(x509Certificate);
        keyInfo.getX509Datas().add(x509Data);
        return keyInfo;
    }

    private KeyName createKeyName(String keyNameValue) {
        final KeyName keyName = (KeyName) XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(KeyName.DEFAULT_ELEMENT_NAME).buildObject(KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(keyNameValue);
        return keyName;
    }
}

package uk.gov.ida.saml.core.test.builders.metadata;

import com.google.common.base.Preconditions;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.signature.KeyInfo;

import java.util.Optional;

public class KeyDescriptorBuilder {

    private String use = "SIGNING";
    private Optional<KeyInfo> keyInfo = Optional.ofNullable(KeyInfoBuilder.aKeyInfo().build());

    public static KeyDescriptorBuilder aKeyDescriptor() {
        return new KeyDescriptorBuilder();
    }

    public KeyDescriptor build() {
        KeyDescriptor keyDescriptor = new org.opensaml.saml.saml2.metadata.impl.KeyDescriptorBuilder().buildObject();
        if (keyInfo.isPresent()) {
            keyDescriptor.setKeyInfo(keyInfo.get());
        }
        keyDescriptor.setUse(UsageType.valueOf(use));
        return keyDescriptor;
    }

    public KeyDescriptorBuilder withUse(String use) {
        Preconditions.checkNotNull(use);
        this.use = use;
        return this;
    }

    public KeyDescriptorBuilder withKeyInfo(KeyInfo keyInfo) {
        this.keyInfo = Optional.ofNullable(keyInfo);
        return this;
    }

    public KeyDescriptorBuilder withX509ForSigning(String certificateValue) {
        withUse("SIGNING");
        return withKeyInfo(KeyInfoBuilder.aKeyInfo().withX509Data(X509DataBuilder.aX509Data().withX509Certificate(X509CertificateBuilder.aX509Certificate().withCert(certificateValue).build()).build()).build());
    }

    public KeyDescriptorBuilder withX509ForEncryption(String certificateValue) {
        withUse("ENCRYPTION");
        return withKeyInfo(KeyInfoBuilder.aKeyInfo().withX509Data(X509DataBuilder.aX509Data().withX509Certificate(X509CertificateBuilder.aX509Certificate().withCert(certificateValue).build()).build()).build());
    }
}

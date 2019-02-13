package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Base64;

public class EncodedCertificateConfiguration extends DeserializablePublicKeyConfiguration {
    @JsonCreator
    public EncodedCertificateConfiguration(@JsonProperty("cert") String encodedCert, @JsonProperty("name") String name) {
        this.fullCertificate = new String(Base64.getDecoder().decode(encodedCert));
        this.certificate = getCertificateFromString(fullCertificate);
        this.name = name;
    }
}
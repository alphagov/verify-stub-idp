package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PublicKeyFileConfiguration extends DeserializablePublicKeyConfiguration {

    @JsonCreator
    public PublicKeyFileConfiguration(
        @JsonProperty("cert") @JsonAlias({ "certFile" }) String certFile,
        @JsonProperty("name") String name
    ) {
        try {
            this.fullCertificate = new String(Files.readAllBytes(Paths.get(certFile)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.certificate = getCertificateFromString(fullCertificate);
        this.name = name;
    }
}
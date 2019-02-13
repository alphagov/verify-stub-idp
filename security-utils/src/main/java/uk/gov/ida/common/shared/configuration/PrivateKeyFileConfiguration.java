package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;

public class PrivateKeyFileConfiguration extends PrivateKeyConfiguration {

    @JsonCreator
    public PrivateKeyFileConfiguration(
            @JsonProperty("key") @JsonAlias({ "keyFile" }) String keyFile) {
        try {
            this.privateKey = getPrivateKeyFromBytes(Files.readAllBytes(Paths.get(keyFile)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PrivateKey privateKey;

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}

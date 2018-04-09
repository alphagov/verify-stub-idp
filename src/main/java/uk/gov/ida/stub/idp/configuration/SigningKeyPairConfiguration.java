package uk.gov.ida.stub.idp.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.ida.common.shared.configuration.DeserializablePublicKeyConfiguration;
import uk.gov.ida.common.shared.configuration.PrivateKeyConfiguration;
import uk.gov.ida.common.shared.configuration.PublicKeyFileConfiguration;

import javax.validation.Valid;
import java.security.PrivateKey;
import java.util.Optional;

public class SigningKeyPairConfiguration {

    @Valid
    @JsonProperty
    private String cert;

    @Valid
    @JsonProperty
    private DeserializablePublicKeyConfiguration publicKeyConfiguration;

    @Valid
    @JsonProperty
    private PrivateKeyConfiguration privateKeyConfiguration;

    public String getCert() {
        return Optional.ofNullable(cert).orElseGet(() -> stripHeaders(publicKeyConfiguration.getCert()));
    }

    public PrivateKey getPrivateKey() {
        return privateKeyConfiguration.getPrivateKey();
    }

    private String stripHeaders(String cert) {
        return cert.replace("-----BEGIN CERTIFICATE-----\n", "")
                .replace("-----END CERTIFICATE-----", "")
                .replace("\n", "")
                .trim();
    }
}

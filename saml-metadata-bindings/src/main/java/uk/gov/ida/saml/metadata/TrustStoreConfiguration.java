package uk.gov.ida.saml.metadata;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.security.KeyStore;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = FileBackedTrustStoreConfiguration.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value=FileBackedTrustStoreConfiguration.class, name="file"),
        @JsonSubTypes.Type(value=EncodedTrustStoreConfiguration.class, name="encoded")
})
public abstract class TrustStoreConfiguration {
    @NotNull
    @Valid
    @JsonProperty
    @JsonAlias({ "password" })
    protected String trustStorePassword;

    public abstract KeyStore getTrustStore();
}

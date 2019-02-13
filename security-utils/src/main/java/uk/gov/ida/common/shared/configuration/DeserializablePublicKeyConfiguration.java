package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.gov.ida.common.shared.security.exceptions.CertificateLoadingException;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/* There is a bug being tracked here - https://github.com/FasterXML/jackson-databind/issues/1358
preventing us from deserializing directly to one of the JsonSubTypes when there is a defaultImpl
defined. This can be avoided by only ever to deserialize to the super type (DeserializablePublicKeyConfiguration) */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    defaultImpl = PublicKeyFileConfiguration.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value=PublicKeyFileConfiguration.class, name="file"),
    @JsonSubTypes.Type(value=EncodedCertificateConfiguration.class, name="encoded"),
    @JsonSubTypes.Type(value=X509CertificateConfiguration.class, name="x509")
})
public abstract class DeserializablePublicKeyConfiguration {

    protected String fullCertificate;
    protected Certificate certificate;
    protected String name;

    public PublicKey getPublicKey() {
        return certificate.getPublicKey();
    }

    public String getName() {
        return name;
    }

    public String getCert() {
        return fullCertificate;
    }

    protected static Certificate getCertificateFromString(String cert) {
        try {
            return CertificateFactory.getInstance("X509").generateCertificate(
                    new ByteArrayInputStream(cert.getBytes())
            );
        } catch (CertificateException e) {
            throw new CertificateLoadingException(cert);
        }
    }
}

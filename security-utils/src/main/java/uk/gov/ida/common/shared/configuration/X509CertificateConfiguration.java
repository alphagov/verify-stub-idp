package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static java.text.MessageFormat.format;
import static uk.gov.ida.common.shared.security.Certificate.BEGIN_CERT;
import static uk.gov.ida.common.shared.security.Certificate.END_CERT;

public class X509CertificateConfiguration extends DeserializablePublicKeyConfiguration {
    @JsonCreator
    public X509CertificateConfiguration(@JsonProperty("cert") @JsonAlias({ "x509" }) String cert) {
        this.fullCertificate = format("{0}\n{1}\n{2}", BEGIN_CERT, cert.trim(), END_CERT);
        this.certificate = getCertificateFromString(fullCertificate);
    }
}
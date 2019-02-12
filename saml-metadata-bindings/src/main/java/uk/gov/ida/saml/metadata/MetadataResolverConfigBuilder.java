package uk.gov.ida.saml.metadata;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.util.Base64;
import uk.gov.ida.saml.metadata.exception.TrustAnchorConfigException;

import javax.ws.rs.core.UriBuilder;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.ida.saml.metadata.ResourceEncoder.entityIdAsResource;

public class MetadataResolverConfigBuilder {

    private KeyStoreLoader keyStoreLoader = new KeyStoreLoader();

    public MetadataResolverConfiguration createMetadataResolverConfiguration(JWK trustAnchor, EidasMetadataConfiguration configuration)
            throws CertificateException{
        return new TrustStoreBackedMetadataConfiguration(
                fullUri(configuration.getMetadataSourceUri(), trustAnchor.getKeyID()),
                configuration.getMinRefreshDelay(),
                configuration.getMaxRefreshDelay(),
                null,
                configuration.getJerseyClientConfiguration(),
                configuration.getJerseyClientName(),
                null,
                trustStoreConfig(trustAnchor)
        );
    }

    private URI fullUri(URI sourceUri, String entityId) {
        return UriBuilder
                .fromUri(sourceUri)
                .path(entityIdAsResource(entityId))
                .build();
    }

    private DynamicTrustStoreConfiguration trustStoreConfig(JWK trustAnchor) throws CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        List<Certificate> certs = trustAnchor.getX509CertChain()
                .stream()
                .map(Base64::decode)
                .map(ByteArrayInputStream::new)
                .map(certStream -> {
                    try { //Java streams don't allow throwing checked exceptions
                        return (X509Certificate) certificateFactory.generateCertificate(certStream);
                    } catch (CertificateException e) {
                        throw new TrustAnchorConfigException("Certificate in Trust Anchor x5c is not a valid x509", e);
                    }
                })
                .collect(Collectors.toList());

        return new DynamicTrustStoreConfiguration(keyStoreLoader.load(certs));
    }
}


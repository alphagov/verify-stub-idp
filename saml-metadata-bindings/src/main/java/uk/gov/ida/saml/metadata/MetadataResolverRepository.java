package uk.gov.ida.saml.metadata;

import com.nimbusds.jose.jwk.JWK;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MetadataResolverRepository {
    Optional<MetadataResolver> getMetadataResolver(String entityId);

    List<String> getResolverEntityIds();

    Optional<ExplicitKeySignatureTrustEngine> getSignatureTrustEngine(String entityId);

    Map<String, MetadataResolver> getMetadataResolvers();

    List<String> getTrustAnchorsEntityIds();

    void refresh();

    List<X509Certificate> sortCertsByDate(JWK trustAnchor);
}

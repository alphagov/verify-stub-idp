package uk.gov.ida.saml.metadata;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.nimbusds.jose.jwk.JWK;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DisabledMetadataResolverRepository implements MetadataResolverRepository{
    @Override
    public Optional<MetadataResolver> getMetadataResolver(String entityId) {
        return Optional.empty();
    }

    @Override
    public List<String> getResolverEntityIds() {
        return ImmutableList.of();
    }

    @Override
    public Optional<ExplicitKeySignatureTrustEngine> getSignatureTrustEngine(String entityId) {
        return Optional.empty();
    }

    @Override
    public Map<String, MetadataResolver> getMetadataResolvers() {
        return ImmutableMap.of();
    }

    @Override
    public List<String> getTrustAnchorsEntityIds() {
        return ImmutableList.of();
    }

    @Override
    public void refresh() { }

    @Override
    public List<X509Certificate> sortCertsByDate(JWK trustAnchor) {
        return ImmutableList.of();
    }
}

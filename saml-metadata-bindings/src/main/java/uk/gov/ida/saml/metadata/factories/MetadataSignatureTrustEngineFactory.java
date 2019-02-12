package uk.gov.ida.saml.metadata.factories;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.PredicateRoleDescriptorResolver;
import org.opensaml.saml.security.impl.MetadataCredentialResolver;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;

public class MetadataSignatureTrustEngineFactory {
    public ExplicitKeySignatureTrustEngine createSignatureTrustEngine(MetadataResolver metadataResolver) throws ComponentInitializationException {
        PredicateRoleDescriptorResolver roleDescriptorResolver = new PredicateRoleDescriptorResolver(metadataResolver);
        roleDescriptorResolver.initialize();
        MetadataCredentialResolver metadataCredentialResolver = new MetadataCredentialResolver();
        metadataCredentialResolver.setRoleDescriptorResolver(roleDescriptorResolver);
        metadataCredentialResolver.setKeyInfoCredentialResolver(DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver());
        metadataCredentialResolver.initialize();
        return new ExplicitKeySignatureTrustEngine(
                metadataCredentialResolver, DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver()
        );
    }
}

package uk.gov.ida.saml.security;


import net.shibboleth.utilities.java.support.resolver.Criterion;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.impl.StaticCredentialResolver;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;

import javax.xml.namespace.QName;
import java.util.Arrays;
import java.util.List;

public class CredentialFactorySignatureValidator extends SignatureValidator {
    private final SigningCredentialFactory credentialFactory;


    public CredentialFactorySignatureValidator(SigningCredentialFactory credentialFactory) {
        this.credentialFactory = credentialFactory;
    }

    @Override
    protected List<Criterion> getAdditionalCriteria(String entityId, QName role) {
        return Arrays.asList(new Criterion() {});
    }

    @Override
    protected TrustEngine<Signature> getTrustEngine(String entityId) {
        List<Credential> credentials = credentialFactory.getVerifyingCredentials(entityId);

        CredentialResolver credResolver = new StaticCredentialResolver(credentials);
        KeyInfoCredentialResolver kiResolver = DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver();
        return new ExplicitKeySignatureTrustEngine(credResolver, kiResolver);
    }
}

package uk.gov.ida.eidas.metadata;

import net.shibboleth.utilities.java.support.resolver.Criterion;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.credential.impl.StaticCredentialResolver;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;

import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;

import javax.xml.namespace.QName;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.List;

public class MetadataSignatureValidator extends SignatureValidator {
    private final Credential credential;

    public MetadataSignatureValidator(PublicKey publickey, PrivateKey privateKey) {
        this.credential = getSigningCredential(publickey, privateKey);
    }

    public boolean validate(SignableSAMLObject signableSAMLObject) throws SignatureException, SecurityException {
        return super.validate(signableSAMLObject, null, null);
    }

    @Override
    protected TrustEngine<Signature> getTrustEngine(String entityId) {
        CredentialResolver credResolver = new StaticCredentialResolver(credential);
        KeyInfoCredentialResolver kiResolver = DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver();
        return new ExplicitKeySignatureTrustEngine(credResolver, kiResolver);
    }

    @Override
    protected List<Criterion> getAdditionalCriteria(String entityId, QName role) {
        return Collections.emptyList();
    }

    private Credential getSigningCredential(PublicKey publicKey, PrivateKey privateKey) {
        BasicCredential credential = new BasicCredential(publicKey, privateKey);
        credential.setUsageType(UsageType.SIGNING);
        return credential;
    }
}

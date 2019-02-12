package uk.gov.ida.saml.security;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;

import javax.xml.namespace.QName;
import java.text.MessageFormat;
import java.util.Optional;

public class MetadataBackedEncryptionCredentialResolver implements EncryptionCredentialResolver {

    private CredentialResolver credentialResolver;
    private QName role;

    public MetadataBackedEncryptionCredentialResolver(CredentialResolver credentialResolver, QName role) {
        this.credentialResolver = credentialResolver;
        this.role = role;
    }

    @Override
    public Credential getEncryptingCredential(String receiverId) {
        CriteriaSet criteria = new CriteriaSet();
        criteria.add(new EntityIdCriterion(receiverId));
        criteria.add(new EntityRoleCriterion(role));
        criteria.add(new UsageCriterion(UsageType.ENCRYPTION));
        try {
            return Optional.ofNullable(credentialResolver.resolveSingle(criteria))
                    .orElseThrow(() -> new CredentialMissingInMetadataException(receiverId));
        } catch (ResolverException e) {
            throw new RuntimeException(e);
        }
    }

    public static class CredentialMissingInMetadataException extends RuntimeException {

        public static final String PATTERN = "No public key for entity-id: \"{0}\" could be found in the metadata. Metadata could be expired, invalid, or missing entities";

        public CredentialMissingInMetadataException(String receivedId) {
           super(MessageFormat.format(PATTERN, receivedId));
        };

    }
}

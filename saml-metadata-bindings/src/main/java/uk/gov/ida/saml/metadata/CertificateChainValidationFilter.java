package uk.gov.ida.saml.metadata;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.common.shared.security.verification.CertificateChainValidator;
import uk.gov.ida.saml.metadata.exception.CertificateConversionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.namespace.QName;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;

import static org.opensaml.xmlsec.keyinfo.KeyInfoSupport.getCertificates;

public final class CertificateChainValidationFilter implements MetadataFilter {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateChainValidationFilter.class);

    private final QName role;
    private final CertificateChainValidator certificateChainValidator;
    private final KeyStore keyStore;

    public CertificateChainValidationFilter(
        @NotNull final QName role,
        @NotNull final CertificateChainValidator certificateChainValidator,
        @NotNull final KeyStore keyStore) {

        this.role = role;
        this.certificateChainValidator = certificateChainValidator;
        this.keyStore = keyStore;
    }

    public QName getRole() {
        return role;
    }

    public CertificateChainValidator getCertificateChainValidator() {
        return certificateChainValidator;
    }

    private KeyStore getKeyStore() {
        return keyStore;
    }

    @Nullable
    @Override
    public XMLObject filter(@Nullable XMLObject metadata) {
        if (metadata == null) {
            return null;
        }

        try {
            if (metadata instanceof EntityDescriptor) {
                EntityDescriptor entityDescriptor = (EntityDescriptor) metadata;
                filterOutUntrustedRoleDescriptors(entityDescriptor);
                if (entityDescriptor.getRoleDescriptors().isEmpty()) {
                    LOG.warn("EntityDescriptor '{}' has empty role descriptor list, metadata will be filtered out", entityDescriptor.getEntityID());
                    return null;
                }
            } else if (metadata instanceof EntitiesDescriptor) {
                EntitiesDescriptor entitiesDescriptor = (EntitiesDescriptor) metadata;
                filterOutUntrustedEntityDescriptors(entitiesDescriptor);
                if (entitiesDescriptor.getEntityDescriptors().isEmpty()) {
                    LOG.warn("EntitiesDescriptor '{}' has empty entity descriptor list, metadata will be filtered out");
                    return null;
                }
            } else {
                LOG.error("Internal error, metadata object was of an unsupported type: {}", metadata.getClass().getName());
                return null;
            }
        } catch (CertificateConversionException e) {
            LOG.error("Saw fatal error validating certificate chain, metadata will be filtered out", e);
            return null;
        }

        return metadata;
    }

    private void filterOutUntrustedEntityDescriptors(@Nonnull EntitiesDescriptor entitiesDescriptor) {
        final String name = getGroupName(entitiesDescriptor);
        LOG.trace("Processing EntitiesDescriptor group: {}", name);

        // Can't use IndexedXMLObjectChildrenList sublist iterator remove() to remove members,
        // so just note them in a set and then remove after iteration has completed.
        final HashSet<XMLObject> toRemove = new HashSet<>();

        entitiesDescriptor.getEntityDescriptors().forEach(
        entityDescriptor -> {
            filterOutUntrustedRoleDescriptors(entityDescriptor);
            if (entityDescriptor.getRoleDescriptors().isEmpty()) {
                LOG.warn("EntityDescriptor '{}' has empty role descriptor list, removing from metadata", entityDescriptor.getEntityID());
                toRemove.add(entityDescriptor);
            }
        });

        if (!toRemove.isEmpty()) {
            entitiesDescriptor.getEntityDescriptors().removeAll(toRemove);
            toRemove.clear();
        }
    }


    private void filterOutUntrustedRoleDescriptors(@Nonnull EntityDescriptor entityDescriptor) {
        final String entityID = entityDescriptor.getEntityID();
        LOG.trace("Processing EntityDescriptor: {}", entityID);

        // Note that this is ok since we're iterating over an IndexedXMLObjectChildrenList directly,
        // rather than a sublist like in processEntityGroup, and iterator remove() is supported there.
        entityDescriptor.getRoleDescriptors()
            .removeIf(roleDescriptor -> {
                if (getRole().equals(roleDescriptor.getElementQName())) {
                    filterOutUntrustedKeyDescriptors(roleDescriptor);
                    if (roleDescriptor.getKeyDescriptors().isEmpty()) {
                        LOG.warn("KeyDescriptor '{}' has empty key descriptor list, removing from metadata", entityID);
                        return true;
                    }
                }
                return false;
            });
    }

    private void filterOutUntrustedKeyDescriptors(@Nonnull RoleDescriptor roleDescriptor) {
        roleDescriptor.getKeyDescriptors().removeIf(
            keyDescriptor -> {
                KeyInfo keyInfo = keyDescriptor.getKeyInfo();
                try {
                    for (final X509Certificate certificate : getCertificates(keyInfo)) {
                        if (!getCertificateChainValidator().validate(certificate, getKeyStore()).isValid()) {
                            LOG.warn("Certificate chain validation failed for metadata entry {}", certificate.getSubjectDN());
                            return true;
                        }
                    }
                    return false;
                } catch (CertificateException e) {
                    throw new CertificateConversionException(e);
                }
            }
        );
    }

    private String getGroupName(final EntitiesDescriptor group) {
        String name = group.getName();
        if (name != null) {
            return name;
        }
        name = group.getID();
        if (name != null) {
            return name;
        }
        return "(unnamed)";
    }
}

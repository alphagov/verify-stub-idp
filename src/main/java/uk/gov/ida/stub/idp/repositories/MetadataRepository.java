package uk.gov.ida.stub.idp.repositories;

import com.google.common.base.Throwables;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.signature.X509Certificate;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

public class MetadataRepository {

    private static final String SUPPORTED_PROTOCOL = "urn:oasis:names:tc:SAML:2.0:protocol";
    private static final String HTTP_POST = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
    private final MetadataResolver metadataResolver;
    private final String hubEntityId;

    @Inject
    public MetadataRepository(MetadataResolver metadataResolver, @Named("HubEntityId") String hubEntityId) {
        this.metadataResolver = metadataResolver;
        this.hubEntityId = hubEntityId;
    }

    private List<AssertionConsumerService> getAssertionConsumerServiceBindings() {
        SPSSODescriptor spssoDescriptor = hubEntityDescriptor().getSPSSODescriptor(SUPPORTED_PROTOCOL);
        return spssoDescriptor.getAssertionConsumerServices();
    }

    public Iterable<String> getSigningCertificates() {
        return hubEntityDescriptor().getSPSSODescriptor(SUPPORTED_PROTOCOL).getKeyDescriptors().stream()
                .filter(input -> input.getUse().equals(UsageType.SIGNING))
                .map(keyDescriptor -> keyDescriptor.getKeyInfo().getX509Datas().get(0).getX509Certificates().get(0).getValue())
                .collect(Collectors.toList());
    }

    private EntityDescriptor hubEntityDescriptor() {
        try {
            CriteriaSet criteria = new CriteriaSet(new EntityIdCriterion(hubEntityId));
            EntityDescriptor hubEntityDescriptor = metadataResolver.resolveSingle(criteria);
			if (hubEntityDescriptor == null) {
				throw new InvalidMetadataException("Federation metadata does not contain a hub entity descriptor");
			}
            return hubEntityDescriptor;
        } catch (ResolverException e) {
            throw new InvalidMetadataException("Federation message is invalid", e);
        }
    }

    public String getEncryptionCertificate() {
        List<KeyDescriptor> keyDescriptors = hubEntityDescriptor().getSPSSODescriptor(SUPPORTED_PROTOCOL).getKeyDescriptors();
        KeyDescriptor keyDescriptor = keyDescriptors.stream().filter(input -> input.getUse().equals(UsageType.ENCRYPTION)).findFirst().get();
        X509Certificate x509Certificate = keyDescriptor.getKeyInfo().getX509Datas().get(0).getX509Certificates().get(0);
        return x509Certificate.getValue();
    }

    public URI getAssertionConsumerServiceLocation() {
        for (AssertionConsumerService endpointDto : getAssertionConsumerServiceBindings()) {
            if (HTTP_POST.equals(endpointDto.getBinding())) {
                try {
                    return new URI(endpointDto.getLocation());
                } catch (URISyntaxException e) {
                    Throwables.propagate(e);
                }
            }
        }
        throw new UnsupportedOperationException();
    }

    public static class InvalidMetadataException extends RuntimeException {
        public InvalidMetadataException(String message, Exception cause) {
            super(message, cause);
        }
        
        public InvalidMetadataException(String message) {
            super(message);
        }
    }
}

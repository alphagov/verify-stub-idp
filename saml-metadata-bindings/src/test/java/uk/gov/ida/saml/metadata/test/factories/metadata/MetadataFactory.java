package uk.gov.ida.saml.metadata.test.factories.metadata;

import com.google.common.base.Throwables;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.gov.ida.shared.utils.xml.XmlUtils;

import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

import static uk.gov.ida.shared.utils.xml.XmlUtils.newDocumentBuilder;

public class MetadataFactory {
    private final EntitiesDescriptorFactory entitiesDescriptorFactory = new EntitiesDescriptorFactory();

    public String defaultMetadata() {
        return metadata(entitiesDescriptorFactory.defaultEntitiesDescriptor());
    }

    public String emptyMetadata() {
        return metadata(entitiesDescriptorFactory.emptyEntitiesDescriptor());
    }

    public String metadata(EntitiesDescriptor entitiesDescriptor) {
        return XmlUtils.writeToString(transform(entitiesDescriptor));
    }

    public String metadata(List<EntityDescriptor> entityDescriptors) {
        return metadata(entitiesDescriptorFactory.entitiesDescriptor(entityDescriptors));
    }

    public String singleEntityMetadata(EntityDescriptor entityDescriptor) {
        return XmlUtils.writeToString(transform(entityDescriptor));
    }

    public String expiredMetadata() {
        return metadata(entitiesDescriptorFactory.expiredEntitiesDescriptor());
    }

    public String unsignedMetadata() {
        return metadata(entitiesDescriptorFactory.unsignedEntitiesDescriptor());
    }

    public String signedMetadata(String publicCertificate, String privateKey) {
        return metadata(entitiesDescriptorFactory.signedEntitiesDescriptor(publicCertificate, privateKey));
    }

    public String metadataWithFullCertificateChain(String publicCertificate, List<String> certificateChain ,String privateKey) {
        return metadata(
                entitiesDescriptorFactory.fullChainSignedEntitiesDescriptor(publicCertificate, certificateChain, privateKey)
        );
    }

    private Element transform(SAMLObject entitiesDescriptor) {
        Element result;
        try {
            marshallToXml(entitiesDescriptor);
            result = entitiesDescriptor.getDOM();
        } catch (ParserConfigurationException | MarshallingException e) {
            throw Throwables.propagate(e);
        }
        return result;
    }

    private Document marshallToXml(SAMLObject samlXml) throws ParserConfigurationException, MarshallingException {
        MarshallerFactory marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();

        Marshaller responseMarshaller = marshallerFactory.getMarshaller(samlXml);

        Document document = newDocumentBuilder().newDocument();
        responseMarshaller.marshall(samlXml, document);

        return document;
    }
}

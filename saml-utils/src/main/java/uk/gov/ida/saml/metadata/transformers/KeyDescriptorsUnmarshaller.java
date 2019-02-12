package uk.gov.ida.saml.metadata.transformers;

import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.xmlsec.signature.X509Data;
import uk.gov.ida.common.shared.security.Certificate;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class KeyDescriptorsUnmarshaller {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory;

    public KeyDescriptorsUnmarshaller(OpenSamlXmlObjectFactory openSamlXmlObjectFactory) {
        this.openSamlXmlObjectFactory = openSamlXmlObjectFactory;
    }

    public List<KeyDescriptor> fromCertificates(Collection<Certificate> certificateDtos) {
        List<KeyDescriptor> keyDescriptors = new ArrayList<>();
        for (Certificate certificateDto : certificateDtos) {
            KeyDescriptor keyDescriptor = openSamlXmlObjectFactory.createKeyDescriptor(certificateDto.getKeyUse().toString());
            KeyInfo keyInfo = openSamlXmlObjectFactory.createKeyInfo(certificateDto.getIssuerId());
            keyDescriptor.setKeyInfo(keyInfo);
            X509Data x509Data = openSamlXmlObjectFactory.createX509Data();
            final X509Certificate x509Certificate = openSamlXmlObjectFactory.createX509Certificate(certificateDto.getCertificate());
            x509Data.getX509Certificates().add(x509Certificate);
            keyInfo.getX509Datas().add(x509Data);
            keyDescriptors.add(keyDescriptor);
        }
        return keyDescriptors;
    }
}

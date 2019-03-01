package uk.gov.ida.stub.idp.builders;

import javax.inject.Inject;
import javax.inject.Named;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;

import uk.gov.ida.saml.security.SignatureFactory;

public class CountryMetadataSigningHelper {
    private SignatureFactory signatureFactory;

    @Inject
    public CountryMetadataSigningHelper(@Named("countryMetadataSignatureFactory") SignatureFactory signatureFactory) {
        this.signatureFactory = signatureFactory;
    }

    public <T extends SignableSAMLObject> T sign(T signableSAMLObject) throws MarshallingException, SignatureException {
        signableSAMLObject.setSignature(signatureFactory.createSignature());
        XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(signableSAMLObject).marshall(signableSAMLObject);
        Signer.signObject(signableSAMLObject.getSignature());

        return signableSAMLObject;
    }
}

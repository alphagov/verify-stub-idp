package uk.gov.ida.eidas.metadata;

import net.shibboleth.utilities.java.support.xml.XMLParserException;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.xmlsec.signature.support.SignatureException;
import uk.gov.ida.eidas.metadata.saml.SamlObjectSigner;
import uk.gov.ida.saml.deserializers.parser.SamlObjectParser;

import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class ConnectorMetadataSigner {
    private PrivateKey key;
    private X509Certificate certificate;
    private AlgorithmType algorithm;

    public ConnectorMetadataSigner(X509Certificate certificate, PrivateKey key, AlgorithmType algorithm) {
        this.key = key;
        this.certificate = certificate;
        this.algorithm = algorithm;
    }

    public SignableSAMLObject sign(String metadataString) throws CertificateEncodingException,
                                                                 XMLParserException,
                                                                 UnmarshallingException,
                                                                 MarshallingException,
                                                                 SignatureException {
        SamlObjectParser sop = new SamlObjectParser();
        SignableSAMLObject metadata = sop.getSamlObject(metadataString);

        String certificateString = Base64.getEncoder().encodeToString(this.certificate.getEncoded());
        SamlObjectSigner samlObjectSigner = new SamlObjectSigner(
                this.certificate.getPublicKey(),
                key,
                certificateString,
                algorithm.getAlgorithmURI());
        samlObjectSigner.sign(metadata);

        return metadata;
    }
}

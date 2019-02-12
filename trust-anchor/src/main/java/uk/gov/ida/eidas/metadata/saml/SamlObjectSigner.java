package uk.gov.ida.eidas.metadata.saml;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;

import javax.xml.namespace.QName;
import java.security.PrivateKey;
import java.security.PublicKey;

public class SamlObjectSigner {
    private final PublicKey publicKey;
    private final PrivateKey privateKey;
    private final String certificate;
    private final SamlObjectMarshaller marshaller;
    private String signatureAlgorithm;

    public SamlObjectSigner(PublicKey publicKey, PrivateKey privateKey, String certificate, String signatureAlgorithm) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.certificate = certificate;
        this.signatureAlgorithm = signatureAlgorithm;
        this.marshaller = new SamlObjectMarshaller();
    }

    public void sign(SignableSAMLObject signableSAMLObject) throws MarshallingException, SignatureException {
        Signature signature = buildSignature();
        signableSAMLObject.setSignature(signature);

        marshaller.marshallToElement(signableSAMLObject);
        Signer.signObject(signature);
    }

    private Signature buildSignature() {
        Signature signature = build(Signature.DEFAULT_ELEMENT_NAME);
        signature.setSignatureAlgorithm(signatureAlgorithm);
        signature.setSigningCredential(new BasicCredential(publicKey, privateKey));
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        signature.setKeyInfo(buildKeyInfo());
        return signature;
    }

    private KeyInfo buildKeyInfo() {
        KeyInfo keyInfo = build(KeyInfo.DEFAULT_ELEMENT_NAME);
        X509Data x509Data = build(X509Data.DEFAULT_ELEMENT_NAME);
        X509Certificate x509Certificate = build(X509Certificate.DEFAULT_ELEMENT_NAME);

        x509Certificate.setValue(certificate);
        x509Data.getX509Certificates().add(x509Certificate);
        keyInfo.getX509Datas().add(x509Data);

        return keyInfo;
    }

    public static <T extends XMLObject> T build(QName elementName) {
        return (T) XMLObjectSupport.buildXMLObject(elementName);
    }
}

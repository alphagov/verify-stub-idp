package uk.gov.ida.saml.security;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.DocumentInternalIDContentReference;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.slf4j.event.Level;
import uk.gov.ida.saml.core.validation.SamlTransformationErrorException;

import javax.validation.constraints.NotNull;
import java.security.cert.X509Certificate;

public class SignatureFactory {
    private final IdaKeyStoreCredentialRetriever keyStoreCredentialRetriever;
    private final SignatureAlgorithm signatureAlgorithm;
    private final DigestAlgorithm digestAlgorithm;
    private final boolean includeKeyInfo;

    public SignatureFactory(IdaKeyStoreCredentialRetriever keyStoreCredentialRetriever, SignatureAlgorithm signatureAlgorithm, DigestAlgorithm digestAlgorithm) {
        this(false, keyStoreCredentialRetriever, signatureAlgorithm, digestAlgorithm);
    }

    public SignatureFactory(boolean includeKeyInfo, IdaKeyStoreCredentialRetriever keyStoreCredentialRetriever, SignatureAlgorithm signatureAlgorithm, DigestAlgorithm digestAlgorithm) {
        this.includeKeyInfo = includeKeyInfo;
        this.keyStoreCredentialRetriever = keyStoreCredentialRetriever;
        this.signatureAlgorithm = signatureAlgorithm;
        this.digestAlgorithm = digestAlgorithm;
    }

    public Signature createSignature() {
        Credential signingCredential = keyStoreCredentialRetriever.getSigningCredential();
        X509Certificate signingCertificate = keyStoreCredentialRetriever.getSigningCertificate();
        Signature signature = (Signature) XMLObjectProviderRegistrySupport.getBuilderFactory()
                .getBuilder(Signature.DEFAULT_ELEMENT_NAME)
                .buildObject(Signature.DEFAULT_ELEMENT_NAME);

        if (includeKeyInfo) {
            if (signingCertificate == null) {
                throw new SamlTransformationErrorException("Unable to generate key info without a signing certificate", Level.ERROR);
            }
            X509KeyInfoGeneratorFactory x509KeyInfoGeneratorFactory = new X509KeyInfoGeneratorFactory();
            x509KeyInfoGeneratorFactory.setEmitEntityCertificate(true);
            KeyInfoGenerator keyInfoGenerator = x509KeyInfoGeneratorFactory.newInstance();
            try {
                signature.setKeyInfo(keyInfoGenerator.generate(new BasicX509Credential(signingCertificate)));
            } catch (SecurityException e) {
                throw new SamlTransformationErrorException("Unable to add signature KeyInfo: ", e, Level.ERROR);
            }
        }

        signature.setSigningCredential(signingCredential);
        signature.setSignatureAlgorithm(signatureAlgorithm.getURI());
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        return signature;
    }

    public Signature createSignature(@NotNull String id) {
        Signature signature = createSignature();
        DocumentInternalIDContentReference contentReference = new DocumentInternalIDContentReference(id);
        contentReference.getTransforms().add(SignatureConstants.TRANSFORM_ENVELOPED_SIGNATURE);
        contentReference.getTransforms().add(SignatureConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        contentReference.setDigestAlgorithm(digestAlgorithm.getURI());
        signature.getContentReferences().add(contentReference);
        return signature;
    }
}

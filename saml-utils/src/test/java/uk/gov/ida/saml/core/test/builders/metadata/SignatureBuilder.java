package uk.gov.ida.saml.core.test.builders.metadata;

import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA256;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA256;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.xmlsec.signature.support.DocumentInternalIDContentReference;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.core.test.TestCredentialFactory;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SignatureBuilder {
    private Credential signingCredential = new TestCredentialFactory(TestCertificateStrings.TEST_PUBLIC_CERT, TestCertificateStrings.TEST_PRIVATE_KEY).getSigningCredential();
    private String id = null;
    private SignatureAlgorithm signatureAlgorithm = new SignatureRSASHA256();
    private DigestAlgorithm digestAlgorithm = new DigestSHA256();
    private List<String> x509Data = new ArrayList<>();

    public static SignatureBuilder aSignature() {
        return new SignatureBuilder();
    }

    public SignatureBuilder withSigningCredential(Credential signingCredential) {
        if (signingCredential != null) {
            this.signingCredential = signingCredential;
        }
        return this;
    }

    public SignatureBuilder withSignatureAlgorithm(SignatureAlgorithm signatureAlgorithm) {
        if (signatureAlgorithm != null) {
            this.signatureAlgorithm = signatureAlgorithm;
        }
        return this;
    }

    public SignatureBuilder withDigestAlgorithm(@NotNull String id, @NotNull DigestAlgorithm digestAlgorithm) {
        this.id = id;
        this.digestAlgorithm = digestAlgorithm;
        return this;
    }

    public SignatureBuilder withX509Data(String publicCertificate) {
        this.x509Data.add(publicCertificate);
        return this;
    }

    public SignatureBuilder withX509Data(List<String> publicCertificate) {
        this.x509Data.addAll(publicCertificate);
        return this;
    }

    public Signature build() {
        final XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
        final XMLObjectBuilder<Signature> signatureBuilder = builderFactory.getBuilderOrThrow(Signature.DEFAULT_ELEMENT_NAME);
        Signature signature = signatureBuilder.buildObject(Signature.DEFAULT_ELEMENT_NAME);
        signature.setSigningCredential(signingCredential);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        signature.setSignatureAlgorithm(signatureAlgorithm.getURI());

        if (id != null) {
            DocumentInternalIDContentReference contentReference = new DocumentInternalIDContentReference(id);
            contentReference.getTransforms().add(SignatureConstants.TRANSFORM_ENVELOPED_SIGNATURE);
            contentReference.getTransforms().add(SignatureConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
            contentReference.setDigestAlgorithm(digestAlgorithm.getURI());
            signature.getContentReferences().add(contentReference);
        }

        if (!x509Data.isEmpty()) {
            X509CertificateBuilder x509CertificateBuilder = X509CertificateBuilder.aX509Certificate();

            x509Data.forEach(x509CertificateBuilder::withCert);

            List<X509Certificate> certs = x509Data.stream()
                    .map(cert -> X509CertificateBuilder.aX509Certificate().withCert(cert).build())
                    .collect(Collectors.toList());

            signature.setKeyInfo(new KeyInfoBuilder().withX509Data(new X509DataBuilder().withX509Certificates(certs).build()).build());
        }
        return signature;
    }
}

package uk.gov.ida.saml.metadata;

import com.google.common.base.Throwables;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.opensaml.saml.metadata.resolver.filter.impl.SignatureValidationFilter;
import org.opensaml.security.x509.impl.BasicPKIXValidationInformation;
import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.keyinfo.impl.BasicProviderKeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.provider.InlineX509DataProvider;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.SignatureValidationParametersCriterion;
import org.opensaml.xmlsec.signature.support.impl.PKIXSignatureTrustEngine;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256_MGF1;
import static org.opensaml.xmlsec.signature.support.SignatureConstants.ALGO_ID_DIGEST_SHA256;
import static org.opensaml.xmlsec.signature.support.SignatureConstants.ALGO_ID_DIGEST_SHA512;
import static org.opensaml.xmlsec.signature.support.SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256;
import static org.opensaml.xmlsec.signature.support.SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA384;
import static org.opensaml.xmlsec.signature.support.SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA512;
import static org.opensaml.xmlsec.signature.support.SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256;
import static org.opensaml.xmlsec.signature.support.SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512;

public class PKIXSignatureValidationFilterProvider implements Provider<SignatureValidationFilter> {

    /*
     Having a chain depth of 0 indicates the certificate used to validate the signature must be issued by a CA present
     in the @metadataTruststore. To arrive at the value 0 we count the nodes between the signature certificate and the
     trust anchor.
     */
    public static final int CERTIFICATE_CHAIN_DEPTH = 0;

    public static final List<String> WHITELISTED_ALGORITHMS = asList(
            ALGO_ID_SIGNATURE_RSA_SHA256,
            ALGO_ID_SIGNATURE_RSA_SHA512,
            ALGO_ID_DIGEST_SHA256,
            ALGO_ID_DIGEST_SHA512,
            ALGO_ID_SIGNATURE_ECDSA_SHA256,
            ALGO_ID_SIGNATURE_ECDSA_SHA384,
            ALGO_ID_SIGNATURE_ECDSA_SHA512,
            ALGO_ID_SIGNATURE_RSA_SHA256_MGF1
    );
    private KeyStore metadataTrustStore;

    @Inject
    public PKIXSignatureValidationFilterProvider(@Named("metadataTruststore") KeyStore metadataTrustStore) {
        Security.addProvider(new BouncyCastleProvider());
        this.metadataTrustStore = metadataTrustStore;
    }

    @Override
    public SignatureValidationFilter get() {
        ArrayList<String> aliases;
        BasicPKIXValidationInformation basicPKIXValidationInformation = null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            aliases = Collections.list(metadataTrustStore.aliases());
            ArrayList<X509Certificate> trustAnchors = new ArrayList<>();
            for (String alias : aliases) {
                trustAnchors.add((X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(metadataTrustStore.getCertificate(alias).getEncoded())));
            }
            basicPKIXValidationInformation = new BasicPKIXValidationInformation(trustAnchors, Collections.emptyList(), CERTIFICATE_CHAIN_DEPTH);
        } catch (KeyStoreException | CertificateException e) {
            Throwables.propagate(e);
        }
        SignatureTrustEngine trustEngine = new PKIXSignatureTrustEngine(
                new NamelessPKIXValidationInformationResolver(asList(basicPKIXValidationInformation)),
                new BasicProviderKeyInfoCredentialResolver(asList(new InlineX509DataProvider()))
        );


        SignatureValidationParameters signatureValidationParameters = new SignatureValidationParameters();
        signatureValidationParameters.setWhitelistedAlgorithms(WHITELISTED_ALGORITHMS);

        SignatureValidationFilter validationFilter = new SignatureValidationFilter(trustEngine);
        validationFilter.setDefaultCriteria(new CriteriaSet(new SignatureValidationParametersCriterion(signatureValidationParameters)));

        validationFilter.setRequireSignedRoot(true);
        return validationFilter;
    }
}

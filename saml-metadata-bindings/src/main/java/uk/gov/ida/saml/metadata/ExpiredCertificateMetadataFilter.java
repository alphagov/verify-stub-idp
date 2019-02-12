package uk.gov.ida.saml.metadata;

import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;
import org.joda.time.DateTimeUtils;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.xmlsec.signature.SignableXMLObject;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.X509Certificate;
import uk.gov.ida.saml.metadata.exception.CertificateConversionException;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ExpiredCertificateMetadataFilter implements MetadataFilter {

    CertificateFactory certificateFactory;

    @Inject
    public ExpiredCertificateMetadataFilter() {
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }
 
    @Override
    public XMLObject filter(XMLObject metadata) throws FilterException {
        SignableXMLObject signableMetadata = (SignableXMLObject) metadata;
        Signature signature = signableMetadata.getSignature();

        List<X509Certificate> certs = signature.getKeyInfo().getX509Datas().stream()
                .flatMap(x -> x.getX509Certificates().stream())
                .collect(Collectors.toList());

        for (X509Certificate cert: certs) {
            java.security.cert.X509Certificate x509Cert = convertToSunCert(cert);
            try {
                x509Cert.checkValidity(new Date(DateTimeUtils.currentTimeMillis()));
            } catch (CertificateExpiredException | CertificateNotYetValidException e) {
                throw new FilterException(e);
            }
        }
        return metadata;
    }

    private java.security.cert.X509Certificate convertToSunCert(X509Certificate cert) {
        try {
            byte[] derValue = Base64.decode(cert.getValue());
            return (java.security.cert.X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(derValue));
        } catch (Base64DecodingException | CertificateException e) {
            throw new CertificateConversionException(e);
        }
    }
}

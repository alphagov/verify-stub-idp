package uk.gov.ida.common.shared.security.verification;

import uk.gov.ida.common.shared.security.X509CertificateFactory;
import uk.gov.ida.common.shared.security.verification.exceptions.CertificateChainValidationException;

import javax.inject.Inject;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;

import static com.google.common.collect.ImmutableList.of;


public class CertificateChainValidator {
    private static final String PKIX_ALGORITHM = "PKIX";
    private static final String X509_CERTIFICATE_TYPE = "X.509";
    private final CertificateFactory certificateFactory;
    private final CertPathValidator certPathValidator;
    private final PKIXParametersProvider pkixParametersProvider;
    private final X509CertificateFactory x509certificateFactory;

    @Inject
    public CertificateChainValidator(
            PKIXParametersProvider pkixParametersProvider,
            X509CertificateFactory x509certificateFactory) {

        this.pkixParametersProvider = pkixParametersProvider;
        this.x509certificateFactory = x509certificateFactory;

        try {
            certificateFactory = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
        } catch (CertificateException e) {
            throw new CertificateChainValidationException(MessageFormat.format("Error retrieving {0} certificate factory instance.", X509_CERTIFICATE_TYPE), e);
        }

        try {
            certPathValidator = CertPathValidator.getInstance(PKIX_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new CertificateChainValidationException(MessageFormat.format("Error retrieving {0} certificate path validator instance.", PKIX_ALGORITHM), e);
        }
    }

    public CertificateValidity validate(X509Certificate certificate, KeyStore trustStore) {
        CertPath certificatePath;

        try {
            certificatePath = certificateFactory.generateCertPath(of(certificate));
        } catch (CertificateException e) {
            throw new CertificateChainValidationException("Error generating certificate path for certificate: " + getDnForCertificate(certificate), e);
        }

        try {
            certPathValidator.validate(certificatePath, pkixParametersProvider.getPkixParameters(trustStore));
        } catch (CertPathValidatorException e) {
            return CertificateValidity.invalid(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new CertificateChainValidationException("Unable to proceed in validating certificate chain: " + getDnForCertificate(certificate), e);
        }
        return CertificateValidity.valid();
    }

    public CertificateValidity validate(String x509String, KeyStore trustStore) {
        X509Certificate x509Certificate = x509certificateFactory.createCertificate(x509String);
        return validate(x509Certificate, trustStore);
    }

    private String getDnForCertificate(X509Certificate certificate) {
        if (certificate != null && certificate.getSubjectDN() != null) {
            return certificate.getSubjectDN().getName();
        }
        return "Unable to get DN";
    }
}

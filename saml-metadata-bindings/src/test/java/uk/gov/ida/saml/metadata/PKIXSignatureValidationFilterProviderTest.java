package uk.gov.ida.saml.metadata;


import certificates.values.CACertificates;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import net.shibboleth.utilities.java.support.xml.XMLParserException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.impl.SignatureValidationFilter;
import org.opensaml.xmlsec.algorithm.descriptors.DigestMD5;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA256;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSAMD5;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA256;
import org.opensaml.xmlsec.signature.Signature;
import uk.gov.ida.saml.core.IdaSamlBootstrap;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.core.test.builders.metadata.SignatureBuilder;
import uk.gov.ida.saml.metadata.test.factories.metadata.EntitiesDescriptorFactory;
import uk.gov.ida.saml.metadata.test.factories.metadata.MetadataFactory;
import uk.gov.ida.saml.metadata.test.factories.metadata.TestCredentialFactory;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PKIXSignatureValidationFilterProviderTest {

    private MetadataFactory metadataFactory = new MetadataFactory();
    private static KeyStoreLoader keyStoreLoader = new KeyStoreLoader();

    private KeyStore trustStore;
    private SignatureValidationFilter signatureValidationFilter;


    private static KeyStore loadKeyStore(List<String> certificates) throws Exception {
        List<Certificate> certificateList = new ArrayList<>();
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        for (String certificate : certificates) {
            Certificate cert = certificateFactory.generateCertificate(IOUtils.toInputStream(certificate));
            certificateList.add(cert);
        }

        return keyStoreLoader.load(certificateList);
    }

    @Before
    public void setUp() throws Exception {
        IdaSamlBootstrap.bootstrap();
        trustStore = loadKeyStore(asList(CACertificates.TEST_METADATA_CA));
        signatureValidationFilter = new PKIXSignatureValidationFilterProvider(trustStore).get();
    }

    @Test
    public void shouldFailValidationIfKeystoreIsEmpty() throws Exception {
        trustStore = loadKeyStore(Collections.emptyList());
        signatureValidationFilter = new PKIXSignatureValidationFilterProvider(trustStore).get();
        assertThatThrownBy(()-> validateMetadata(metadataFactory.defaultMetadata())).isInstanceOf(FilterException.class);
    }

    @Test
    public void shouldFailToFilterMetadataWithNoSignature() {
        assertThatThrownBy(()-> validateMetadata(metadataFactory.unsignedMetadata())).isInstanceOf(FilterException.class);
    }

    @Test
    public void shouldSucceedLoadingValidMetadataAgainstCertificatesFromTheConfiguration() throws Exception {
        XMLObject metadata = validateMetadata(metadataFactory.defaultMetadata());
        assertThat(metadata).isNotNull().withFailMessage("Metadata should not be filtered out");
    }

    @Test
    public void shouldSucceedLoadingValidMetadataWhenSignedWithAlternateCertificate() throws Exception {
        XMLObject metadata = validateMetadata(metadataFactory.signedMetadata(TestCertificateStrings.METADATA_SIGNING_B_PUBLIC_CERT, TestCertificateStrings.METADATA_SIGNING_B_PRIVATE_KEY));
        assertThat(metadata).isNotNull().withFailMessage("Metadata should not be filtered out");
    }

    @Test
    public void shouldErrorLoadingInvalidMetadataWhenSignedWithCertificateIssuedByOtherCA() throws Exception {
        assertThatThrownBy(()-> {
            validateMetadata(metadataFactory.signedMetadata(TestCertificateStrings.HUB_TEST_PUBLIC_SIGNING_CERT, TestCertificateStrings.HUB_TEST_PRIVATE_SIGNING_KEY));
        }).isInstanceOf(FilterException.class);
    }

    @Test
    public void shouldLoadMetadataWhenSignedWithGoodSignatureAlgorithm() throws Exception {
        Signature signature = SignatureBuilder.aSignature()
                .withSignatureAlgorithm(new SignatureRSASHA256())
                .withX509Data(TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT)
                .withSigningCredential(new TestCredentialFactory(TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT, TestCertificateStrings.METADATA_SIGNING_A_PRIVATE_KEY).getSigningCredential()).build();

        XMLObject metadata = validateMetadata(metadataFactory.metadata(new EntitiesDescriptorFactory().signedEntitiesDescriptor(signature)));
        assertThat(metadata).isNotNull().withFailMessage("Metadata should not be filtered out");
    }

    @Test
    public void shouldErrorLoadingInvalidMetadataWhenSignedWithBadSignatureAlgorithm() throws Exception {
        Signature signature = SignatureBuilder.aSignature()
                .withSignatureAlgorithm(new SignatureRSAMD5())
                .withX509Data(TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT)
                .withSigningCredential(new TestCredentialFactory(TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT, TestCertificateStrings.METADATA_SIGNING_A_PRIVATE_KEY).getSigningCredential()).build();
        String metadataContent = metadataFactory.metadata(new EntitiesDescriptorFactory().signedEntitiesDescriptor(signature));

        assertThatThrownBy(()-> validateMetadata(metadataContent)).isInstanceOf(FilterException.class);
    }

    @Test
    public void shouldSucceedLoadingMetadataWhenSignedWithGoodDigestAlgorithm() throws Exception {
        DigestSHA256 digestAlgorithm = new DigestSHA256();

        String id = UUID.randomUUID().toString();
        Signature signature = SignatureBuilder.aSignature()
                .withDigestAlgorithm(id, digestAlgorithm)
                .withX509Data(TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT)
                .withSigningCredential(new TestCredentialFactory(TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT, TestCertificateStrings.METADATA_SIGNING_A_PRIVATE_KEY).getSigningCredential()).build();
        XMLObject metadata = validateMetadata(metadataFactory.metadata(new EntitiesDescriptorFactory().signedEntitiesDescriptor(id, signature)));
        assertThat(metadata).isNotNull().withFailMessage("Metadata should not be filtered out");
    }

    @Test
    public void shouldErrorLoadingInvalidMetadataWhenSignedWithBadDigestAlgorithm() throws Exception {
        String id = UUID.randomUUID().toString();
        Signature signature = SignatureBuilder.aSignature()
                .withDigestAlgorithm(id, new DigestMD5())
                .withX509Data(TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT)
                .withSigningCredential(new TestCredentialFactory(TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT, TestCertificateStrings.METADATA_SIGNING_A_PRIVATE_KEY).getSigningCredential()).build();
        String metadataContent = metadataFactory.metadata(new EntitiesDescriptorFactory().signedEntitiesDescriptor(id, signature));

        assertThatThrownBy(()-> validateMetadata(metadataContent)).isInstanceOf(FilterException.class);
    }

    @Test
    public void shouldErrorLoadingMetadataWhenTrustStoreOnlyContainsRootCertificate() throws Exception {
        trustStore = loadKeyStore(asList(CACertificates.TEST_ROOT_CA));
        signatureValidationFilter = new PKIXSignatureValidationFilterProvider(trustStore).get();

        String metadataContent = metadataFactory.metadataWithFullCertificateChain(
                TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT,
                Arrays.asList(
                        TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT,
                        createInlineCertificate(CACertificates.TEST_METADATA_CA)),
                TestCertificateStrings.METADATA_SIGNING_A_PRIVATE_KEY);

        assertThatThrownBy(()-> validateMetadata(metadataContent)).isInstanceOf(FilterException.class);
    }

    @Test
    public void shouldErrorLoadingInvalidMetadataAgainstCertificatesFromTheConfiguration() {
        String metadataContent = metadataFactory.signedMetadata(TestCertificateStrings.UNCHAINED_PUBLIC_CERT, TestCertificateStrings.UNCHAINED_PRIVATE_KEY);
        assertThatThrownBy(()-> validateMetadata(metadataContent)).isInstanceOf(FilterException.class);
    }

    private XMLObject validateMetadata(String metadataContent) throws XMLParserException, UnmarshallingException, FilterException, ComponentInitializationException {
        BasicParserPool parserPool = new BasicParserPool();
        parserPool.initialize();
        XMLObject metadata = XMLObjectSupport.unmarshallFromInputStream(parserPool, IOUtils.toInputStream(metadataContent));
        return signatureValidationFilter.filter(metadata);
    }

    private static String createInlineCertificate(String pemString) {
        String BEGIN = "-----BEGIN CERTIFICATE-----\n";
        String END = "\n-----END CERTIFICATE-----";
        return pemString.substring(pemString.lastIndexOf(BEGIN) + BEGIN.length(), pemString.indexOf(END));
    }

}

package uk.gov.ida.stub.idp.repositories;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import org.joda.time.DateTime;
import org.junit.Test;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import uk.gov.ida.saml.core.test.TestEntityIds;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static uk.gov.ida.saml.core.test.TestCertificateStrings.HUB_TEST_PUBLIC_ENCRYPTION_CERT;
import static uk.gov.ida.saml.core.test.TestCertificateStrings.HUB_TEST_PUBLIC_SIGNING_CERT;
import static uk.gov.ida.saml.core.test.TestCertificateStrings.TEST_RP_PUBLIC_SIGNING_CERT;

public class MetadataRepositoryTest {

    public static final String ENCRYPTION_CERTIFICATE = HUB_TEST_PUBLIC_ENCRYPTION_CERT;

    public static final String SIGNING_CERTIFICATE_1 = HUB_TEST_PUBLIC_SIGNING_CERT;

    public static final String SIGNING_CERTIFICATE_2 = TEST_RP_PUBLIC_SIGNING_CERT;

    public static final String LOCATION = "http://localhost:50190/SAML2/SSO/Response/POST";

    public static final String METADATA_PATTERN = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<md:EntitiesDescriptor xmlns:md=\"urn:oasis:names:tc:SAML:2.0:metadata\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ID=\"_entities\">\n" +
            "    <md:EntityDescriptor ID=\"_9efc2cf0-bca2-43c6-94a4-348a929515d4\" entityID=\"https://signin.service.gov.uk\" validUntil=\"{0}\" xsi:type=\"md:EntityDescriptorType\">\n" +
            "        <md:SPSSODescriptor protocolSupportEnumeration=\"urn:oasis:names:tc:SAML:2.0:protocol\" xsi:type=\"md:SPSSODescriptorType\">\n" +
            "            <md:KeyDescriptor use=\"signing\" xsi:type=\"md:KeyDescriptorType\">\n" +
            "                <ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xsi:type=\"ds:KeyInfoType\">\n" +
            "                    <ds:KeyName xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">https://signin.service.gov.uk</ds:KeyName>\n" +
            "                    <ds:X509Data xsi:type=\"ds:X509DataType\">\n" +
            "                        <ds:X509Certificate>{2}</ds:X509Certificate>\n" +
            "                    </ds:X509Data>\n" +
            "                </ds:KeyInfo>\n" +
            "            </md:KeyDescriptor>\n" +
            "            <md:KeyDescriptor use=\"signing\" xsi:type=\"md:KeyDescriptorType\">\n" +
            "                <ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xsi:type=\"ds:KeyInfoType\">\n" +
            "                    <ds:KeyName xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">https://signin.service.gov.uk/another-key</ds:KeyName>\n" +
            "                    <ds:X509Data xsi:type=\"ds:X509DataType\">\n" +
            "                        <ds:X509Certificate>{3}</ds:X509Certificate>\n" +
            "                    </ds:X509Data>\n" +
            "                </ds:KeyInfo>\n" +
            "            </md:KeyDescriptor>\n" +
            "            <md:KeyDescriptor use=\"encryption\" xsi:type=\"md:KeyDescriptorType\">\n" +
            "                <ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xsi:type=\"ds:KeyInfoType\">\n" +
            "                    <ds:KeyName xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">https://signin.service.gov.uk</ds:KeyName>\n" +
            "                    <ds:X509Data xsi:type=\"ds:X509DataType\">\n" +
            "                        <ds:X509Certificate>{1}</ds:X509Certificate>\n" +
            "                    </ds:X509Data>\n" +
            "                </ds:KeyInfo>\n" +
            "            </md:KeyDescriptor>\n" +
            "            <md:AssertionConsumerService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Location=\"{4}\" index=\"1\" isDefault=\"true\" xsi:type=\"md:IndexedEndpointType\"/>\n" +
            "        </md:SPSSODescriptor>\n" +
            "        <md:Organization xsi:type=\"md:OrganizationType\">\n" +
            "            <md:OrganizationName xml:lang=\"en-GB\" xsi:type=\"md:localizedNameType\">GOV.UK</md:OrganizationName>\n" +
            "            <md:OrganizationDisplayName xml:lang=\"en-GB\" xsi:type=\"md:localizedNameType\">GOV.UK</md:OrganizationDisplayName>\n" +
            "            <md:OrganizationURL xml:lang=\"en-GB\" xsi:type=\"md:localizedURIType\">https://www.gov.uk</md:OrganizationURL>\n" +
            "        </md:Organization>\n" +
            "    </md:EntityDescriptor>\n" +
            "</md:EntitiesDescriptor>\n";

    public static final String METADATA_WITHOUT_HUB_PATTERN = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<md:EntitiesDescriptor xmlns:md=\"urn:oasis:names:tc:SAML:2.0:metadata\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ID=\"_entities\">\n" +
            "</md:EntitiesDescriptor>\n";

    public static final String DEFAULT_METADATA = MessageFormat.format(METADATA_PATTERN, DateTime.now().withDurationAdded(10000, 1), ENCRYPTION_CERTIFICATE, SIGNING_CERTIFICATE_1, SIGNING_CERTIFICATE_2, LOCATION);
    public static final String METADATA_WITHUOUT_HUB = MessageFormat.format(METADATA_WITHOUT_HUB_PATTERN, DateTime.now().withDurationAdded(10000, 1));

    private MetadataRepository metadataRepository;

    @Test
    public void shouldReturnTheAssertionConsumerService() throws Exception {
        metadataRepository = initializeMetadata(DEFAULT_METADATA);

        assertThat(metadataRepository.getAssertionConsumerServiceLocation()).isEqualTo(URI.create(LOCATION));
    }
    @Test
     public void shouldReturnTheEncryptionCertificateMetadata() throws Exception {
        metadataRepository = initializeMetadata(DEFAULT_METADATA);

        assertThat(metadataRepository.getEncryptionCertificate()).isEqualTo(ENCRYPTION_CERTIFICATE);
    }

    @Test
    public void shouldReturnTheSigningCertificates() throws Exception {
        metadataRepository = initializeMetadata(DEFAULT_METADATA);

        Iterable<String> signingCertificates = metadataRepository.getSigningCertificates();
        assertThat(signingCertificates).containsExactly(SIGNING_CERTIFICATE_1, SIGNING_CERTIFICATE_2);
    }

    @Test
    public void shouldThrowExpiredExceptionIfMetadataIsOld() throws Exception {
        DateTime expiredDateTime = new DateTime(2001, 1, 1, 0, 0);
        metadataRepository = initializeMetadata(MessageFormat.format(METADATA_PATTERN, expiredDateTime, ENCRYPTION_CERTIFICATE, SIGNING_CERTIFICATE_1, SIGNING_CERTIFICATE_2, LOCATION));
        try {
            metadataRepository.getSigningCertificates();
            fail("Expected exception");
        } catch (MetadataRepository.InvalidMetadataException e) {
        }
    }

    @Test
    public void shouldThrowInvalidExceptionIfMetadataIsMissingHubEntityDescriptor() throws Exception {
        metadataRepository = initializeMetadata(METADATA_WITHUOUT_HUB);
        try {
            metadataRepository.getSigningCertificates();
            fail("Expected exception");
        } catch (MetadataRepository.InvalidMetadataException e) {
        }
    }

    private MetadataRepository initializeMetadata(String metadata) throws IOException, InitializationException, ComponentInitializationException, ResolverException {
        File metadataFile = File.createTempFile("metadata", ".xml");
        FileWriter fileWriter = new FileWriter(metadataFile);
        fileWriter.write(metadata);
        fileWriter.flush();
        InitializationService.initialize();
        FilesystemMetadataResolver filesystemMetadataResolver = new FilesystemMetadataResolver(metadataFile);
        BasicParserPool pool = new BasicParserPool();
        pool.initialize();
        filesystemMetadataResolver.setParserPool(pool);
        filesystemMetadataResolver.setRequireValidMetadata(true);
        filesystemMetadataResolver.setId("some id");
        filesystemMetadataResolver.initialize();
        return new MetadataRepository(filesystemMetadataResolver, TestEntityIds.HUB_ENTITY_ID);
    }

}

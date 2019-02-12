package uk.gov.ida.saml.metadata;


import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.deserializers.OpenSamlXMLObjectUnmarshaller;
import uk.gov.ida.saml.deserializers.parser.SamlObjectParser;
import uk.gov.ida.saml.metadata.test.factories.metadata.MetadataFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class ExpiredCertificateMetadataFilterTest {

    private MetadataFactory metadataFactory = new MetadataFactory();
    private MetadataFilter metadataFilter;
    private OpenSamlXMLObjectUnmarshaller<XMLObject> unmarshaller = new OpenSamlXMLObjectUnmarshaller<>(new SamlObjectParser());

    @Before
    public void setUp() throws Exception {
        metadataFilter = new ExpiredCertificateMetadataFilter();
        InitializationService.initialize();
    }

    @Test
    public void shouldFailToFilterLoadingValidMetadataWhenSignedWithExpiredCertificate() {
        try {
            DateTimeUtils.setCurrentMillisFixed(DateTime.now().plusYears(1000).getMillis());
            String signedMetadata = metadataFactory.signedMetadata(TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT, TestCertificateStrings.METADATA_SIGNING_A_PRIVATE_KEY);
            XMLObject metadata = unmarshaller.fromString(signedMetadata);
            metadataFilter.filter(metadata);
            fail("Expected exception not thrown");
        } catch (FilterException e){
            assertThat(true).isTrue();
        } finally {
            DateTimeUtils.setCurrentMillisSystem();
        }
    }

    @Test
    public void shouldFailToFilterLoadingValidMetadataWhenSignedWithNotYetValidCertificate() {
        try {
            DateTimeUtils.setCurrentMillisFixed(DateTime.now().minusYears(1000).getMillis());
            String signedMetadata = metadataFactory.signedMetadata(TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT, TestCertificateStrings.METADATA_SIGNING_A_PRIVATE_KEY);
            XMLObject metadata = unmarshaller.fromString(signedMetadata);
            metadataFilter.filter(metadata);
            fail("Expected exception not thrown");
        } catch (FilterException e){
            assertThat(true).isTrue();
        } finally {
            DateTimeUtils.setCurrentMillisSystem();
        }
    }

    @Test
    public void shouldFilterMetadataSuccessfully() throws Exception {
        String signedMetadata = metadataFactory.signedMetadata(TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT, TestCertificateStrings.METADATA_SIGNING_A_PRIVATE_KEY);
        XMLObject metadata = unmarshaller.fromString(signedMetadata);
        metadata = metadataFilter.filter(metadata);
        Assert.assertNotNull("metadata should not have been filtered out", metadata);
    }
}

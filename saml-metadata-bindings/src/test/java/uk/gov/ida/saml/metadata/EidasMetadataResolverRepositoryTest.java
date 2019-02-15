package uk.gov.ida.saml.metadata;

import certificates.values.CACertificates;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import org.slf4j.LoggerFactory;
import uk.gov.ida.common.shared.security.X509CertificateFactory;
import uk.gov.ida.eidas.trustanchor.CountryTrustAnchor;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.metadata.factories.DropwizardMetadataResolverFactory;
import uk.gov.ida.saml.metadata.factories.MetadataSignatureTrustEngineFactory;
import uk.gov.ida.shared.utils.datetime.DateTimeFreezer;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.UriBuilder;
import java.security.KeyStoreException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EidasMetadataResolverRepositoryTest {

    @Mock
    private EidasTrustAnchorResolver trustAnchorResolver;

    @Mock
    private EidasMetadataConfiguration metadataConfiguration;

    @Mock
    private DropwizardMetadataResolverFactory dropwizardMetadataResolverFactory;

    @Mock
    private Timer timer;

    @Mock
    private JerseyClientMetadataResolver metadataResolver;

    @Mock
    private MetadataSignatureTrustEngineFactory metadataSignatureTrustEngineFactory;

    @Mock
    private ExplicitKeySignatureTrustEngine explicitKeySignatureTrustEngine;

    @Mock
    private Client metadataClient;

    @Mock
    private MetadataResolverConfigBuilder metadataResolverConfigBuilder;

    @Captor
    private ArgumentCaptor<MetadataResolverConfiguration> metadataResolverConfigurationCaptor;

    private X509CertificateFactory certificateFactory = new X509CertificateFactory();

    private List<JWK> trustAnchors;

    @Before
    public void setUp() throws CertificateException, SignatureException, ParseException, JOSEException, ComponentInitializationException {
        trustAnchors = new ArrayList<>();
        when(trustAnchorResolver.getTrustAnchors()).thenReturn(trustAnchors);
        when(dropwizardMetadataResolverFactory.createMetadataResolverWithClient(any(), eq(true), eq(metadataClient))).thenReturn(metadataResolver);
        when(metadataSignatureTrustEngineFactory.createSignatureTrustEngine(metadataResolver)).thenReturn(explicitKeySignatureTrustEngine);
    }

    @After
    public void tearDown() {
        DateTimeFreezer.unfreezeTime();
    }

    @Test
    public void shouldCreateMetadataResolverWhenTrustAnchorIsValid() throws KeyStoreException, CertificateEncodingException {
        List<String> stringCertChain = Arrays.asList(
                CACertificates.TEST_ROOT_CA,
                CACertificates.TEST_IDP_CA,
                TestCertificateStrings.STUB_COUNTRY_PUBLIC_PRIMARY_CERT
        );

        String entityId = "http://signin.gov.uk/entity/id";
        JWK trustAnchor = createJWK(entityId, stringCertChain, true);
        trustAnchors.add(trustAnchor);

        when(metadataConfiguration.getMetadataSourceUri()).thenReturn(UriBuilder.fromUri("https://source.com").build());
        EidasMetadataResolverRepository metadataResolverRepository = new EidasMetadataResolverRepository(
                trustAnchorResolver,
                metadataConfiguration,
                dropwizardMetadataResolverFactory,
                timer,
                metadataSignatureTrustEngineFactory,
                new MetadataResolverConfigBuilder(),
                metadataClient);

        verify(dropwizardMetadataResolverFactory).createMetadataResolverWithClient(metadataResolverConfigurationCaptor.capture(), eq(true), eq(metadataClient));
        MetadataResolver createdMetadataResolver = metadataResolverRepository.getMetadataResolver(trustAnchor.getKeyID()).get();
        MetadataResolverConfiguration metadataResolverConfiguration = metadataResolverConfigurationCaptor.getValue();
        byte[] expectedTrustStoreCertificate = trustAnchor.getX509CertChain().get(0).decode();
        byte[] expectedTrustStoreCACertificate = trustAnchor.getX509CertChain().get(1).decode();
        byte[] actualTrustStoreCertificate = metadataResolverConfiguration.getTrustStore().getCertificate("certificate-0").getEncoded();
        byte[] actualTrustStoreCACertificate = metadataResolverConfiguration.getTrustStore().getCertificate("certificate-1").getEncoded();

        assertThat(createdMetadataResolver).isEqualTo(metadataResolver);
        assertArrayEquals(expectedTrustStoreCertificate, actualTrustStoreCertificate);
        assertArrayEquals(expectedTrustStoreCACertificate, actualTrustStoreCACertificate);
        assertThat(metadataResolverConfiguration.getUri().toString()).isEqualTo("https://source.com/" + ResourceEncoder.entityIdAsResource(entityId));
        assertThat(metadataResolverRepository.getSignatureTrustEngine(trustAnchor.getKeyID())).isEqualTo(Optional.of(explicitKeySignatureTrustEngine));
    }

    @Test
    public void shouldUseEarliestExpiryDateOfX509Cert() {
        String entityId = "http://signin.gov.uk/entity-id";

        List<String> stringCertsChain = asList(
                CACertificates.TEST_ROOT_CA,
                CACertificates.TEST_IDP_CA,
                TestCertificateStrings.STUB_COUNTRY_PUBLIC_TERTIARY_CERT
        );

        JWK trustAnchor = createJWK(entityId, stringCertsChain, true);
        trustAnchors.add(trustAnchor);

        when(metadataConfiguration.getMetadataSourceUri()).thenReturn(UriBuilder.fromUri("https://source.com").build());
        EidasMetadataResolverRepository metadataResolverRepository = new EidasMetadataResolverRepository(
                trustAnchorResolver,
                metadataConfiguration,
                dropwizardMetadataResolverFactory,
                timer,
                metadataSignatureTrustEngineFactory,
                new MetadataResolverConfigBuilder(),
                metadataClient);

        verify(dropwizardMetadataResolverFactory).createMetadataResolverWithClient(metadataResolverConfigurationCaptor.capture(), eq(true), eq(metadataClient));

        MetadataResolver createdMetadataResolver = metadataResolverRepository.getMetadataResolver(trustAnchor.getKeyID()).get();
        MetadataResolverConfiguration metadataResolverConfiguration = metadataResolverConfigurationCaptor.getValue();
        metadataResolverConfiguration.getMinRefreshDelay();

        List<X509Certificate> sortedCerts = metadataResolverRepository.sortCertsByDate(trustAnchor);

        assertThat(trustAnchor.getX509CertChain().size()).isEqualTo(3);
        assertThat(createdMetadataResolver).isEqualTo(metadataResolver);
        assertThat(sortedCerts.get(0).getNotAfter()).isBefore(sortedCerts.get(1).getNotAfter());
        assertThat(sortedCerts.get(1).getNotAfter()).isBefore(sortedCerts.get(2).getNotAfter());
        assertThat(sortedCerts.get(0)).isEqualTo(certificateFactory.createCertificate(CACertificates.TEST_ROOT_CA));
    }

    @Test
    public void shouldNotCreateMetadataResolverAndLogWhenCertificateIsExpired() {
        Appender mockAppender = mock(Appender.class);
        Logger logger = (Logger) LoggerFactory.getLogger(EidasMetadataResolverRepository.class);
        logger.addAppender(mockAppender);
        ArgumentCaptor<LoggingEvent> loggingEventCaptor = ArgumentCaptor.forClass(LoggingEvent.class);

        String entityId = "http://signin.gov.uk/entity-id";
        List<String> certificateChain = asList(
            CACertificates.TEST_ROOT_CA,
            CACertificates.TEST_IDP_CA,
            TestCertificateStrings.STUB_COUNTRY_PUBLIC_EXPIRED_CERT
        );
        trustAnchors.add(createJWK(entityId, certificateChain, false));

        EidasMetadataResolverRepository metadataResolverRepository = new EidasMetadataResolverRepository(
            trustAnchorResolver,
            metadataConfiguration,
            dropwizardMetadataResolverFactory,
            timer,
            metadataSignatureTrustEngineFactory,
            new MetadataResolverConfigBuilder(),
            metadataClient);

        assertThat(metadataResolverRepository.getMetadataResolver(entityId)).isEmpty();
        assertThat(metadataResolverRepository.getSignatureTrustEngine(entityId)).isEmpty();
        verify(mockAppender).doAppend(loggingEventCaptor.capture());
        assertThat(loggingEventCaptor.getValue().getMessage())
            .contains(String.format("Error creating MetadataResolver for %s", entityId));
    }

    @Test
    public void shouldNotCreateMetadataResolverRepositoryWhenCertificateIsInvalid() {
        String entityId = "http://signin.gov.uk/entity-id";
        List<String> invalidCertChain = asList(
                CACertificates.TEST_ROOT_CA,
                CACertificates.TEST_IDP_CA,
                TestCertificateStrings.STUB_COUNTRY_PUBLIC_NOT_YET_VALID_CERT
        );
        trustAnchors.add(createJWK(entityId, invalidCertChain, false));

        EidasMetadataResolverRepository metadataResolverRepository = null;
        try {
            metadataResolverRepository = new EidasMetadataResolverRepository(
                trustAnchorResolver,
                metadataConfiguration,
                dropwizardMetadataResolverFactory,
                timer,
                metadataSignatureTrustEngineFactory,
                new MetadataResolverConfigBuilder(),
                metadataClient);
            fail("EidasMetadataResolverRepository should throw an error with an invalid cert in the trust anchor");
        } catch (Error e) {
            assertThat(e.getMessage()).startsWith("Managed to generate an invalid anchor: Certificate CN=IDA Stub Country Signing Dev");
        }

        assertThat(metadataResolverRepository).isNull();
    }

    @Test
    public void shouldAddNewMetadataResolverWhenRefreshing() throws CertificateException, SignatureException, ParseException, JOSEException {
        EidasMetadataResolverRepository metadataResolverRepository = createMetadataResolverRepositoryWithTrustAnchors();

        assertThat(metadataResolverRepository.getTrustAnchorsEntityIds()).hasSize(0);

        List<String> certificateChain = asList(
                CACertificates.TEST_ROOT_CA,
                CACertificates.TEST_METADATA_CA,
                TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT
        );
        JWK trustAnchor1 = createJWK("http://signin.gov.uk/entity/id", certificateChain, true);
        when(trustAnchorResolver.getTrustAnchors()).thenReturn(singletonList(trustAnchor1));
        metadataResolverRepository.refresh();

        assertThat(metadataResolverRepository.getTrustAnchorsEntityIds()).hasSize(1);
    }

    @Test
    public void shouldRemoveOldMetadataResolverWhenRefreshing() throws CertificateException, SignatureException, ParseException, JOSEException {
        List<String> certificateChain = asList(
                CACertificates.TEST_ROOT_CA,
                CACertificates.TEST_METADATA_CA,
                TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT
        );
        JWK trustAnchor1 = createJWK("http://signin.gov.uk/entity/id", certificateChain,true);
        JWK trustAnchor2 = createJWK("http://signin.gov.uk/entity/id", certificateChain,true);

        EidasMetadataResolverRepository metadataResolverRepository = createMetadataResolverRepositoryWithTrustAnchors(trustAnchor1, trustAnchor2);

        assertThat(metadataResolverRepository.getTrustAnchorsEntityIds()).hasSize(2);

        when(trustAnchorResolver.getTrustAnchors()).thenReturn(singletonList(trustAnchor2));
        metadataResolverRepository.refresh();

        assertThat(metadataResolverRepository.getTrustAnchorsEntityIds()).hasSize(1);
        assertThat(metadataResolverRepository.getTrustAnchorsEntityIds()).contains(trustAnchor2.getKeyID());
    }

    @Test
    public void shouldNotRecreateExistingMetadataResolversWhenRefreshing() throws ParseException, CertificateException, JOSEException, SignatureException {
        List<String> certificateChain = asList(
                CACertificates.TEST_ROOT_CA,
                CACertificates.TEST_METADATA_CA,
                TestCertificateStrings.METADATA_SIGNING_A_PUBLIC_CERT
        );
        EidasMetadataResolverRepository metadataResolverRepository = createMetadataResolverRepositoryWithTrustAnchors(createJWK("http://signin.gov.uk/entity/id", certificateChain, true));

        Map<String, MetadataResolver> originalMetadataResolvers = metadataResolverRepository.getMetadataResolvers();
        reset(dropwizardMetadataResolverFactory);
        metadataResolverRepository.refresh();

        verifyZeroInteractions(dropwizardMetadataResolverFactory);
        Map<String, MetadataResolver> refreshedMetadataResolvers = metadataResolverRepository.getMetadataResolvers();
        refreshedMetadataResolvers.forEach((key, value) -> assertThat(value == originalMetadataResolvers.get(key)).isTrue());
    }

    private EidasMetadataResolverRepository createMetadataResolverRepositoryWithTrustAnchors(JWK... trustAnchors) throws ParseException, CertificateException, JOSEException, SignatureException {
        when(trustAnchorResolver.getTrustAnchors()).thenReturn(asList(trustAnchors));

        return new EidasMetadataResolverRepository(
                trustAnchorResolver,
                metadataConfiguration,
                dropwizardMetadataResolverFactory,
                timer,
                metadataSignatureTrustEngineFactory,
                metadataResolverConfigBuilder,
                metadataClient);
    }

    private JWK createJWK(String entityId, List<String> certificates, Boolean validate) {
        List<X509Certificate> certs = certificates.stream().map(certificateFactory::createCertificate).collect(Collectors.toList());
        return CountryTrustAnchor.make(certs, entityId, validate);
    }

}

package uk.gov.ida.eidas.trustanchor;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CertificateSorterTest {

    @Test
    public void shouldReturnEmptyListWhenGivenEmptyList() {
        List<X509Certificate> sortedCerts = CertificateSorter.sort(ImmutableList.of());
        assertThat(sortedCerts).isEmpty();
    }

    @Test
    public void shouldReturnListWhenGivenSingleCert() {
        ImmutableList<X509Certificate> testCertificates = ImmutableList.of(mock(X509Certificate.class));
        List<X509Certificate> sortedCerts = CertificateSorter.sort(testCertificates);

        assertThat(sortedCerts).isEqualTo(testCertificates);
    }

    @Test
    public void shouldReturnSameTwoCertListWhenGivenTwoCertsInOrder() {
        X509Certificate leafCert = mock(X509Certificate.class);
        X509Certificate parentCert = mock(X509Certificate.class);
        X500Principal x500Parent = new X500Principal(principalName("parent"));
        X500Principal x500Leaf = new X500Principal(principalName("leaf"));

        when(leafCert.getIssuerX500Principal()).thenReturn(x500Parent);
        when(leafCert.getSubjectX500Principal()).thenReturn(x500Leaf);
        when(parentCert.getSubjectX500Principal()).thenReturn(x500Parent);

        ImmutableList<X509Certificate> testCertificates = ImmutableList.of(leafCert, parentCert);
        List<X509Certificate> sortedCerts = CertificateSorter.sort(testCertificates);

        assertThat(sortedCerts).isEqualTo(testCertificates);
    }

    @Test
    public void shouldReturnSortedTwoCertListWhenGivenTwoCertsNotInOrder() {
        X509Certificate leafCert = mock(X509Certificate.class);
        X509Certificate parentCert = mock(X509Certificate.class);
        X500Principal x500Parent = new X500Principal(principalName("parent"));
        X500Principal x500Leaf = new X500Principal(principalName("leaf"));

        when(leafCert.getIssuerX500Principal()).thenReturn(x500Parent);
        when(leafCert.getSubjectX500Principal()).thenReturn(x500Leaf);
        when(parentCert.getSubjectX500Principal()).thenReturn(x500Parent);

        ImmutableList<X509Certificate> testCertificates = ImmutableList.of(parentCert, leafCert);
        List<X509Certificate> sortedCerts = CertificateSorter.sort(testCertificates);

        ImmutableList<X509Certificate> controlCertificates = ImmutableList.of(leafCert, parentCert);
        assertThat(sortedCerts).isEqualTo(controlCertificates);
    }

    @Test
    public void shouldReturnSortedThreeCertListWhenGivenThreeCertsInOrder() {
        X509Certificate leafCert = mock(X509Certificate.class);
        X509Certificate intermediaryCert = mock(X509Certificate.class);
        X509Certificate parentCert = mock(X509Certificate.class);
        X500Principal x500Parent = new X500Principal(principalName("parent"));
        X500Principal x500Intermediary = new X500Principal(principalName("intermediary"));
        X500Principal x500Leaf = new X500Principal(principalName("leaf"));

        when(leafCert.getIssuerX500Principal()).thenReturn(x500Intermediary);
        when(leafCert.getSubjectX500Principal()).thenReturn(x500Leaf);
        when(intermediaryCert.getIssuerX500Principal()).thenReturn(x500Parent);
        when(intermediaryCert.getSubjectX500Principal()).thenReturn(x500Intermediary);
        when(parentCert.getSubjectX500Principal()).thenReturn(x500Parent);

        ImmutableList<X509Certificate> testCertificates = ImmutableList.of(leafCert, intermediaryCert, parentCert);
        List<X509Certificate> sortedCerts = CertificateSorter.sort(testCertificates);

        assertThat(sortedCerts).isEqualTo(testCertificates);
    }

    @Test
    public void shouldReturnSortedThreeCertListWhenGivenThreeCertsNotInOrderWithIntermediaryAndParentSwapped() {
        X509Certificate leafCert = mock(X509Certificate.class);
        X509Certificate intermediaryCert = mock(X509Certificate.class);
        X509Certificate parentCert = mock(X509Certificate.class);
        X500Principal x500Parent = new X500Principal(principalName("parent"));
        X500Principal x500Intermediary = new X500Principal(principalName("intermediary"));
        X500Principal x500Leaf = new X500Principal(principalName("leaf"));

        when(leafCert.getIssuerX500Principal()).thenReturn(x500Intermediary);
        when(leafCert.getSubjectX500Principal()).thenReturn(x500Leaf);
        when(intermediaryCert.getIssuerX500Principal()).thenReturn(x500Parent);
        when(intermediaryCert.getSubjectX500Principal()).thenReturn(x500Intermediary);
        when(parentCert.getSubjectX500Principal()).thenReturn(x500Parent);

        ImmutableList<X509Certificate> testCertificates = ImmutableList.of(leafCert, parentCert, intermediaryCert);
        List<X509Certificate> sortedCerts = CertificateSorter.sort(testCertificates);

        ImmutableList<X509Certificate> controlCertificates = ImmutableList.of(leafCert, intermediaryCert, parentCert);
        assertThat(sortedCerts).isEqualTo(controlCertificates);
    }

    @Test
    public void shouldReturnSortedThreeCertListWhenGivenThreeCertsNotInOrderWithLeafAndParentSwapped() {
        X509Certificate leafCert = mock(X509Certificate.class);
        X509Certificate intermediaryCert = mock(X509Certificate.class);
        X509Certificate parentCert = mock(X509Certificate.class);
        X500Principal x500Parent = new X500Principal(principalName("parent"));
        X500Principal x500Intermediary = new X500Principal(principalName("intermediary"));
        X500Principal x500Leaf = new X500Principal(principalName("leaf"));

        when(leafCert.getIssuerX500Principal()).thenReturn(x500Intermediary);
        when(leafCert.getSubjectX500Principal()).thenReturn(x500Leaf);
        when(intermediaryCert.getIssuerX500Principal()).thenReturn(x500Parent);
        when(intermediaryCert.getSubjectX500Principal()).thenReturn(x500Intermediary);
        when(parentCert.getSubjectX500Principal()).thenReturn(x500Parent);

        ImmutableList<X509Certificate> testCertificates = ImmutableList.of(parentCert, intermediaryCert, leafCert);
        List<X509Certificate> sortedCerts = CertificateSorter.sort(testCertificates);

        ImmutableList<X509Certificate> controlCertificates = ImmutableList.of(leafCert, intermediaryCert, parentCert);
        assertThat(sortedCerts).isEqualTo(controlCertificates);
    }

    @Test
    public void shouldReturnSortedThreeCertListWhenGivenThreeCertsNotInOrderWithLeafAndIntermediarySwapped() {
        X509Certificate leafCert = mock(X509Certificate.class);
        X509Certificate intermediaryCert = mock(X509Certificate.class);
        X509Certificate parentCert = mock(X509Certificate.class);
        X500Principal x500Parent = new X500Principal(principalName("parent"));
        X500Principal x500Intermediary = new X500Principal(principalName("intermediary"));
        X500Principal x500Leaf = new X500Principal(principalName("leaf"));

        when(leafCert.getIssuerX500Principal()).thenReturn(x500Intermediary);
        when(leafCert.getSubjectX500Principal()).thenReturn(x500Leaf);
        when(intermediaryCert.getIssuerX500Principal()).thenReturn(x500Parent);
        when(intermediaryCert.getSubjectX500Principal()).thenReturn(x500Intermediary);
        when(parentCert.getSubjectX500Principal()).thenReturn(x500Parent);

        ImmutableList<X509Certificate> testCertificates = ImmutableList.of(intermediaryCert, leafCert, parentCert);
        List<X509Certificate> sortedCerts = CertificateSorter.sort(testCertificates);

        ImmutableList<X509Certificate> controlCertificates = ImmutableList.of(leafCert, intermediaryCert, parentCert);
        assertThat(sortedCerts).isEqualTo(controlCertificates);
    }

    @Test
    public void shouldThrowAnExceptionIfMoreThanOneLeafCerts() {
        X509Certificate leafCert = mock(X509Certificate.class);
        X509Certificate surplusLeafCert = mock(X509Certificate.class);
        X509Certificate parentCert = mock(X509Certificate.class);
        X500Principal x500Parent = new X500Principal(principalName("parent"));
        X500Principal x500Surplus = new X500Principal(principalName("surplus"));
        X500Principal x500Leaf = new X500Principal(principalName("leaf"));

        when(leafCert.getIssuerX500Principal()).thenReturn(x500Parent);
        when(leafCert.getSubjectX500Principal()).thenReturn(x500Leaf);
        when(surplusLeafCert.getIssuerX500Principal()).thenReturn(x500Parent);
        when(surplusLeafCert.getSubjectX500Principal()).thenReturn(x500Surplus);
        when(parentCert.getSubjectX500Principal()).thenReturn(x500Parent);

        ImmutableList<X509Certificate> testCertificates = ImmutableList.of(surplusLeafCert, leafCert, parentCert);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> CertificateSorter.sort(testCertificates));
    }

    @Test
    public void shouldThrowAnExceptionIfDuplicateIssuerCerts() {
        X509Certificate leafCert = mock(X509Certificate.class);
        X509Certificate duplicateParentCert = mock(X509Certificate.class);
        X509Certificate parentCert = mock(X509Certificate.class);
        X500Principal x500Parent = new X500Principal(principalName("parent"));
        X500Principal x500Leaf = new X500Principal(principalName("leaf"));

        when(leafCert.getIssuerX500Principal()).thenReturn(x500Parent);
        when(leafCert.getSubjectX500Principal()).thenReturn(x500Leaf);
        when(parentCert.getSubjectX500Principal()).thenReturn(x500Parent);
        when(duplicateParentCert.getSubjectX500Principal()).thenReturn(x500Parent);

        ImmutableList<X509Certificate> testCertificates = ImmutableList.of(duplicateParentCert, leafCert, parentCert);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> CertificateSorter.sort(testCertificates));
    }

    private String principalName(String id) {
        return "CN=" + id + ", OU=GDS, O=CO, C=UK";
    }
}

package uk.gov.ida.saml.security;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import uk.gov.ida.saml.security.saml.OpenSAMLMockitoRunner;

import static org.mockito.Mockito.when;

@RunWith(OpenSAMLMockitoRunner.class)
public class SignatureFactoryTest {

    @Mock
    private IdaKeyStoreCredentialRetriever idaKeyStoreCredentialRetriever;

    @Mock
    private SignatureAlgorithm signatureAlgorithm;

    @Mock
    private DigestAlgorithm digestAlgorithm;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowExceptionWhenNoSigningCerts() {
        expectedException.expectMessage("Unable to generate key info without a signing certificate");

        SignatureFactory signatureFactory = new SignatureFactory(true, idaKeyStoreCredentialRetriever, signatureAlgorithm, digestAlgorithm);

        when(idaKeyStoreCredentialRetriever.getSigningCredential()).thenReturn(null);
        when(idaKeyStoreCredentialRetriever.getSigningCertificate()).thenReturn(null);

        signatureFactory.createSignature();
    }
}

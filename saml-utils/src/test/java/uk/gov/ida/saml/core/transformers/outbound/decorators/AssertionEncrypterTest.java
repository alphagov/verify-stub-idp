package uk.gov.ida.saml.core.transformers.outbound.decorators;

import org.junit.Test;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.encryption.Encrypter;
import org.opensaml.security.credential.Credential;
import uk.gov.ida.saml.security.EncrypterFactory;
import uk.gov.ida.saml.security.KeyStoreBackedEncryptionCredentialResolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AssertionEncrypterTest {
    @Test
    public void shouldEncryptAssertion() throws Exception {
        KeyStoreBackedEncryptionCredentialResolver credentialFactory = mock(KeyStoreBackedEncryptionCredentialResolver.class);
        final Credential credential = mock(Credential.class);
        String entityId = "my-entity-id";
        when(credentialFactory.getEncryptingCredential(entityId)).thenReturn(credential);

        EncrypterFactory encrypterFactory = mock(EncrypterFactory.class);
        Encrypter encrypter = mock(Encrypter.class);
        when(encrypterFactory.createEncrypter(credential)).thenReturn(encrypter);

        Assertion assertion = mock(Assertion.class);
        EncryptedAssertion expectedEncryptedAssertion = mock(EncryptedAssertion.class);
        when(encrypter.encrypt(assertion)).thenReturn(expectedEncryptedAssertion);

        AssertionEncrypter assertionEncrypter = new AssertionEncrypter(encrypterFactory, credentialFactory);
        assertThat(assertionEncrypter.encrypt(assertion, entityId)).isEqualTo(expectedEncryptedAssertion);
    }

}

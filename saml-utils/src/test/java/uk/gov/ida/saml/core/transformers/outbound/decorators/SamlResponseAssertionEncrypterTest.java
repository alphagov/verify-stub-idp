package uk.gov.ida.saml.core.transformers.outbound.decorators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.encryption.Encrypter;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;
import uk.gov.ida.saml.security.EncrypterFactory;
import uk.gov.ida.saml.security.EntityToEncryptForLocator;
import uk.gov.ida.saml.security.KeyStoreBackedEncryptionCredentialResolver;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(OpenSAMLRunner.class)
public class SamlResponseAssertionEncrypterTest {

    @Test
    public void shouldConvertAssertionIntoEncryptedAssertion() throws EncryptionException {

        KeyStoreBackedEncryptionCredentialResolver credentialFactory = mock(KeyStoreBackedEncryptionCredentialResolver.class);

        Credential credential = mock(Credential.class);
        EntityToEncryptForLocator entityToEncryptForLocator = mock(EntityToEncryptForLocator.class);
        when(entityToEncryptForLocator.fromRequestId(Matchers.anyString())).thenReturn("some id");
        when(credentialFactory.getEncryptingCredential("some id")).thenReturn(credential);


        EncrypterFactory encrypterFactory = mock(EncrypterFactory.class);
        Encrypter encrypter = mock(Encrypter.class);
        when(encrypterFactory.createEncrypter(credential)).thenReturn(encrypter);


        Response response = mock(Response.class);
        Assertion assertion = mock(Assertion.class);
        List<Assertion> assertionList = spy(newArrayList(assertion));
        when(response.getAssertions()).thenReturn(assertionList);

        EncryptedAssertion encryptedAssertion = mock(EncryptedAssertion.class);
        when(encrypter.encrypt(assertion)).thenReturn(encryptedAssertion);
        List<EncryptedAssertion> encryptedAssertionList = spy(new ArrayList<EncryptedAssertion>());
        when(response.getEncryptedAssertions()).thenReturn(encryptedAssertionList);

        SamlResponseAssertionEncrypter assertionEncrypter = new SamlResponseAssertionEncrypter(
                credentialFactory,
                encrypterFactory,
                entityToEncryptForLocator
        );

        when(assertionEncrypter.getRequestId(response)).thenReturn("some id");

        assertionEncrypter.encryptAssertions(response);
        verify(encryptedAssertionList, times(1)).add(encryptedAssertion);
    }

    @Test
    public void decorate_shouldWrapEncryptionAssertionInSamlExceptionWhenEncryptionFails() throws EncryptionException {
        KeyStoreBackedEncryptionCredentialResolver credentialFactory = mock(KeyStoreBackedEncryptionCredentialResolver.class);

        Credential credential = mock(Credential.class);
        EntityToEncryptForLocator entityToEncryptForLocator = mock(EntityToEncryptForLocator.class);
        when(entityToEncryptForLocator.fromRequestId(Matchers.anyString())).thenReturn("some id");
        when(credentialFactory.getEncryptingCredential("some id")).thenReturn(credential);

        Response response = mock(Response.class);
        Assertion assertion = mock(Assertion.class);
        List<Assertion> assertionList = spy(newArrayList(assertion));
        when(response.getAssertions()).thenReturn(assertionList);

        EncrypterFactory encrypterFactory = mock(EncrypterFactory.class);
        Encrypter encrypter = mock(Encrypter.class);
        when(encrypterFactory.createEncrypter(credential)).thenReturn(encrypter);
        EncryptionException encryptionException = new EncryptionException("BLAM!");
        when(encrypter.encrypt(assertion)).thenThrow(encryptionException);

        SamlResponseAssertionEncrypter assertionEncrypter =
                new SamlResponseAssertionEncrypter(credentialFactory, encrypterFactory,
                        entityToEncryptForLocator
                );

        when(assertionEncrypter.getRequestId(response)).thenReturn("some id");

        try {
            assertionEncrypter.encryptAssertions(response);
        } catch (Exception e) {
            assertThat(e.getCause()).isEqualTo(encryptionException);
            return;
        }
        fail("Should never get here");
    }
}

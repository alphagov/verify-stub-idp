package uk.gov.ida.saml.metadata;


import keystore.KeyStoreRule;
import keystore.builders.KeyStoreRuleBuilder;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.metadata.exception.EmptyTrustStoreException;
import uk.gov.ida.saml.metadata.factories.MetadataTrustStoreProvider;

import java.security.KeyStore;
import java.security.KeyStoreException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MetadataTrustStoreProviderTest {

    @Mock
    KeyStoreLoader keyStoreLoader;

    @ClassRule
    public static KeyStoreRule emptyKeyStoreRule = KeyStoreRuleBuilder.aKeyStoreRule().build();

    @ClassRule
    public static KeyStoreRule keyStoreRule = KeyStoreRuleBuilder.aKeyStoreRule().withCertificate("hub", TestCertificateStrings.HUB_TEST_PUBLIC_SIGNING_CERT).build();

    private String filePath ="file path";
    private final String password = "password";

    @Test(expected = EmptyTrustStoreException.class)
    public void shouldThrowExceptionIfTrustStoreContainsNoCertificates() throws KeyStoreException {
        Mockito.when(keyStoreLoader.load(filePath,password)).thenReturn(emptyKeyStoreRule.getKeyStore());
        MetadataTrustStoreProvider metadataTrustStoreProvider = new MetadataTrustStoreProvider(keyStoreLoader, filePath, password);

        metadataTrustStoreProvider.get();
    }

    @Test(expected = RuntimeException.class)
    public void shouldPropagateExceptionIfKeystoreIsUninitialized() throws KeyStoreException {
        Mockito.when(keyStoreLoader.load(filePath,password)).thenReturn(KeyStore.getInstance(KeyStore.getDefaultType()));
        MetadataTrustStoreProvider metadataTrustStoreProvider = new MetadataTrustStoreProvider(keyStoreLoader, filePath, password);

        metadataTrustStoreProvider.get();
    }

    @Test
    public void shouldReturnTrustStoreContainingCertificates() throws KeyStoreException {
        Mockito.when(keyStoreLoader.load(filePath, password)).thenReturn(keyStoreRule.getKeyStore());
        MetadataTrustStoreProvider metadataTrustStoreProvider = new MetadataTrustStoreProvider(keyStoreLoader, filePath, password);

        assertThat(metadataTrustStoreProvider.get().containsAlias("hub")).isTrue();
    }
}

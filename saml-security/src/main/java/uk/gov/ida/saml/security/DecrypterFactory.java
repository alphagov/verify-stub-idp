package uk.gov.ida.saml.security;

import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.encryption.EncryptedElementTypeEncryptedKeyResolver;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.encryption.support.ChainingEncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.InlineEncryptedKeyResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.CollectionKeyInfoCredentialResolver;

import java.util.Arrays;
import java.util.List;

public class DecrypterFactory {
    public Decrypter createDecrypter(List<Credential> credentials) {

        // Resolves local credentials by using information in the EncryptedKey/KeyInfo to query the supplied
        // local credential resolver.
        KeyInfoCredentialResolver kekResolver = new CollectionKeyInfoCredentialResolver(credentials);

        EncryptedElementTypeEncryptedKeyResolver encryptedElementTypeEncryptedKeyResolver = new EncryptedElementTypeEncryptedKeyResolver();
        List<EncryptedKeyResolver> encKeyResolvers = Arrays.asList(encryptedElementTypeEncryptedKeyResolver, new InlineEncryptedKeyResolver());

        ChainingEncryptedKeyResolver encryptedKeyResolver = new ChainingEncryptedKeyResolver(encKeyResolvers);

        return new IdaDecrypter(null, kekResolver, encryptedKeyResolver);
    }
}

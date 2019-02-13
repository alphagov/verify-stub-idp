package uk.gov.ida.common.shared.security;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class CryptoHelperTest {

    static final String EXAMPLE_IDP = "http://example.com/idp";
    static final String KEY = base64(new byte[CryptoHelper.KEY_AND_NONCE_AND_IV_LENGTH_IN_BYTES]);
    private static CryptoHelper cryptoHelper;

    private static String base64(byte[] data) {
        return javax.xml.bind.DatatypeConverter.printBase64Binary(data);
    }

    private static byte[] unbase64(String data) {
        return javax.xml.bind.DatatypeConverter.parseBase64Binary(data);
    }

    @BeforeClass
    public static void setUp() throws Exception {
        cryptoHelper = new CryptoHelper(KEY);
    }

    @Test
    public void testInitializationVectorIsCorrectLength() {
        assertThat(cryptoHelper.KEY_AND_NONCE_AND_IV_LENGTH_IN_BYTES).isEqualTo(16);
    }

    @Test
    public void testShouldDecryptToTheOriginalValue() {
        assertThat(EXAMPLE_IDP).isEqualTo(
                cryptoHelper.decrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(cryptoHelper.encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(EXAMPLE_IDP).get()).get());
    }

    @Test
    public void testMultipleEncryptionsOfSameIDPEntityIDResultInDifferentValues() {
        assertThat(cryptoHelper.encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(EXAMPLE_IDP).get()).isNotEqualTo(
                cryptoHelper.encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(EXAMPLE_IDP).get()
        );
    }

    @Test
    public void testEncryptedDataShouldNotContainUnencryptedData() {
        final String encrypted = unbase64(cryptoHelper.encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(EXAMPLE_IDP).get()).toString();
        assertThat(encrypted).doesNotContain(EXAMPLE_IDP);
    }

    @Test
    public void testOrderOfEncryptedNotImportantByDecryptngInADifferentOrder() {
        final int count = 100;
        Map<String, String> encryptedValues = new HashMap<>();
        for (int i=0; i<count; i++) {
            String idpEntityId = EXAMPLE_IDP + "/" + i;
            encryptedValues.put(idpEntityId, cryptoHelper.encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(idpEntityId).get());
        }
        List<Object> shuffledKeys = new ArrayList(encryptedValues.keySet());
        Collections.shuffle(shuffledKeys);
        for(Object key:shuffledKeys) {
            assertThat(key).isEqualTo(cryptoHelper.decrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(encryptedValues.get(key)).get());
        }

    }

    @Test
    public void testEncryptedValuesShouldDecryptRepeatedly() {
        String encryptedValue = cryptoHelper.encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(EXAMPLE_IDP).get();
        for (int i=0; i<100; i++) {
            assertThat(EXAMPLE_IDP).isEqualTo(cryptoHelper.decrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(encryptedValue).get());
        }
     }

    @Test
    public void testCannotDecryptWithChangedKey() {
        String encryptedValue = cryptoHelper.encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(EXAMPLE_IDP).get();
        CryptoHelper otherCryptoHelper = new CryptoHelper(base64("sixteenbyteslong".getBytes()));
        // Catch a BadPaddingException in decrypt
        assertThat(otherCryptoHelper.decrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(encryptedValue).isPresent()).isFalse();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCannotConstructCryptoHelperWithIncorrectKeyLength() {
        new CryptoHelper(base64(new byte[CryptoHelper.KEY_AND_NONCE_AND_IV_LENGTH_IN_BYTES+1]));
    }

    @Test
    public void testShortEntityIDProducesSameLengthEncryptedOutput() {
        String veryLongIdpEntityId = "http://example.com/something/incredibly/long/which/wont/be/beaten/by/a/real/life/entityId/like_______________________________this";
        String standedEntityIDEncryptionValue = cryptoHelper.encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(veryLongIdpEntityId).get();
        String shortEntityIDEncryptionValue = cryptoHelper.encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited("h").get();
        assertThat(unbase64(standedEntityIDEncryptionValue).length).isEqualTo(unbase64(shortEntityIDEncryptionValue).length);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExtremelyLongEntityIDsShouldNotBeAccepted() {
        cryptoHelper.encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(new String(new byte[2000]));
    }

    @Test
    public void testDecryptingEmptyStringReturnsAbsent() {
        assertThat(cryptoHelper.decrypt_yesIKnowThisCryptoCodeHasNotBeenAudited("").isPresent()).isFalse();
    }
}
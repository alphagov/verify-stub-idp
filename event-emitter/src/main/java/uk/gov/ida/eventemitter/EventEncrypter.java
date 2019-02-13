package uk.gov.ida.eventemitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.inject.Inject;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class EventEncrypter implements Encrypter {

    public static final int INITIALISATION_VECTOR_SIZE = 16;
    private final byte[] key;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JodaModule());

    @Inject
    public EventEncrypter(final byte[] key) {
        this.key = key;
    }

    public String encrypt(final Event event) throws EventEncryptionException {

        try {
            final byte[] initialisationVector = generateInitialisationVector();
            final byte[] encryptedEvent = encryptEvent(event, initialisationVector);
            final byte[] encryptedEventAndIv = combineEncryptedEventWithIV(encryptedEvent, initialisationVector);
            return Base64.encodeBase64String(encryptedEventAndIv);
        } catch(Exception e) {
            throw new EventEncryptionException(e);
        }

    }

    private byte[] generateInitialisationVector() {
        byte[] initialisationVector = new byte[INITIALISATION_VECTOR_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(initialisationVector);
        return initialisationVector;
    }

    private byte[] encryptEvent(final Event event, final byte[] initialisationVector) throws Exception {
        final SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(initialisationVector));
        return cipher.doFinal(mapper.writeValueAsBytes(event));
    }

    private byte[] combineEncryptedEventWithIV(final byte[] encryptedEvent, final byte[] initialisationVector) {
        byte[] encryptedEventAndIv = new byte[INITIALISATION_VECTOR_SIZE + encryptedEvent.length];
        System.arraycopy(initialisationVector, 0, encryptedEventAndIv, 0, INITIALISATION_VECTOR_SIZE);
        System.arraycopy(encryptedEvent, 0, encryptedEventAndIv, INITIALISATION_VECTOR_SIZE, encryptedEvent.length);
        return encryptedEventAndIv;
    }
}

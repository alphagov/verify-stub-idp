package uk.gov.ida.eventemitter.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static uk.gov.ida.eventemitter.EventEncrypter.INITIALISATION_VECTOR_SIZE;

public class TestDecrypter<T> {

    private final byte[] key;
    private final ObjectMapper mapper;

    @Inject
    public TestDecrypter(final byte[] key,
                         final ObjectMapper mapper) {
        this.key = key;
        this.mapper = mapper;
    }

    public T decrypt(final String base64EncodedEncryptedEvent, final Class<T> klass) throws Exception {
        final String event = decrypt(base64EncodedEncryptedEvent);
        return (T) mapper.readValue(event, klass);
    }

    public String decrypt(final String bas64EncodedEncryptedEvent) throws Exception {
        final byte[] encryptedEventAndIv = Base64.decodeBase64(bas64EncodedEncryptedEvent);
        final byte[] initialisationVector = extractIv(encryptedEventAndIv);
        final byte[] encryptedEvent = extractEncryptedEvent(encryptedEventAndIv);
        final byte[] event = decryptEncryptedEvent(encryptedEvent, initialisationVector);

        return new String(event);
    }

    private byte[] extractIv(final byte[] encryptedEventAndIv) {
        byte[] initialisationVector = new byte[INITIALISATION_VECTOR_SIZE];
        System.arraycopy(encryptedEventAndIv, 0, initialisationVector, 0, initialisationVector.length);
        return initialisationVector;
    }

    private byte[] extractEncryptedEvent(final byte[] encryptedEventAndIv) {
        int encryptedSize = encryptedEventAndIv.length - INITIALISATION_VECTOR_SIZE;
        byte[] encryptedEvent = new byte[encryptedSize];
        System.arraycopy(encryptedEventAndIv, INITIALISATION_VECTOR_SIZE, encryptedEvent, 0, encryptedSize);
        return encryptedEvent;
    }

    private byte[] decryptEncryptedEvent(final byte[] encryptedEvent, final byte[] initialisationVector) throws Exception {
        final SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(initialisationVector));
        return cipher.doFinal(encryptedEvent);
    }
}

package uk.gov.ida.eventemitter;

public interface Encrypter {

    String encrypt(final Event event) throws EventEncryptionException;
}

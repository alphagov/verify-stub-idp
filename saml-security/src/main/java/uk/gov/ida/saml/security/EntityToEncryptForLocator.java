package uk.gov.ida.saml.security;

/*
* This class is used by the response encryptors to find out who sent the original request
* in order to properly encrypt the message for the relying party
* */
public interface EntityToEncryptForLocator {
    String fromRequestId(String requestId);
}

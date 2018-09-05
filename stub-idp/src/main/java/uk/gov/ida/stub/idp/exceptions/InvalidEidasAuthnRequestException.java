package uk.gov.ida.stub.idp.exceptions;

public class InvalidEidasAuthnRequestException extends RuntimeException {

    public InvalidEidasAuthnRequestException(String messsage) {
        super("Invalid Eidas Authn Request: " + messsage);
    }
}

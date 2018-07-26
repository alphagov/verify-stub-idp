package uk.gov.ida.stub.idp.exceptions;

public class IdpUserNotFoundException extends RuntimeException {
    public IdpUserNotFoundException(String message) {
        super(message);
    }
}

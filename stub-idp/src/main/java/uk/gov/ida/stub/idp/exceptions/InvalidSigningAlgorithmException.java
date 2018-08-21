package uk.gov.ida.stub.idp.exceptions;

public class InvalidSigningAlgorithmException extends RuntimeException {

    public InvalidSigningAlgorithmException(String signingAlgorithm) {
        super("Invalid Signing Algorithm " + signingAlgorithm);
    }
}

package uk.gov.ida.stub.idp.exceptions;

import javax.ws.rs.core.Response;
import java.util.Optional;

public class GenericStubIdpException extends RuntimeException {
    private final Optional<Response.Status> responseStatus;

    public GenericStubIdpException(String message, Response.Status responseStatus) {
        super(message);
        this.responseStatus = Optional.ofNullable(responseStatus);
    }

    public Optional<Response.Status> getResponseStatus() {
        return responseStatus;
    }
}

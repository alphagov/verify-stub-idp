package uk.gov.ida.stub.idp.exceptions.mappers;

import uk.gov.ida.stub.idp.exceptions.IdpNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class IdpNotFoundExceptionMapper implements ExceptionMapper<IdpNotFoundException> {
    @Override
    public Response toResponse(IdpNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
    }
}

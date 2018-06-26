package uk.gov.ida.stub.idp.exceptions.mappers;

import org.apache.log4j.Logger;
import uk.gov.ida.stub.idp.exceptions.IdpNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class IdpNotFoundExceptionMapper implements ExceptionMapper<IdpNotFoundException> {
    private static final Logger LOG = Logger.getLogger(IdpNotFoundExceptionMapper.class);
    @Override
    public Response toResponse(IdpNotFoundException exception) {
        LOG.error(exception);
        return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
    }
}

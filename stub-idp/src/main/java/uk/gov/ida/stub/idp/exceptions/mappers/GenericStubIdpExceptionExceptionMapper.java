package uk.gov.ida.stub.idp.exceptions.mappers;

import org.apache.log4j.Logger;
import uk.gov.ida.stub.idp.exceptions.GenericStubIdpException;
import uk.gov.ida.stub.idp.views.ErrorPageView;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class GenericStubIdpExceptionExceptionMapper implements ExceptionMapper<GenericStubIdpException> {
    private static final Logger LOG = Logger.getLogger(GenericStubIdpExceptionExceptionMapper.class);
    @Override
    public Response toResponse(GenericStubIdpException exception) {
        LOG.error(exception);
        return Response
                .status(exception.getResponseStatus().orElse(Response.Status.INTERNAL_SERVER_ERROR))
                .entity(new ErrorPageView())
                .build();
    }
}

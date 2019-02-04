package uk.gov.ida.stub.idp.exceptions.mappers;

import org.apache.log4j.Logger;
import uk.gov.ida.stub.idp.views.ErrorPageView;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class WebApplicationExceptionExceptionMapper implements ExceptionMapper<WebApplicationException> {
    private static final Logger LOG = Logger.getLogger(WebApplicationExceptionExceptionMapper.class);
    @Override
    public Response toResponse(WebApplicationException exception) {
        LOG.error(exception);
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorPageView())
                .build();
    }
}

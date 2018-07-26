package uk.gov.ida.stub.idp.exceptions.mappers;

import org.apache.log4j.Logger;
import uk.gov.ida.stub.idp.views.ErrorPageView;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class CatchAllExceptionMapper implements ExceptionMapper<RuntimeException> {
    private static final Logger LOG = Logger.getLogger(CatchAllExceptionMapper.class);
    @Override
    public Response toResponse(RuntimeException exception) {
        LOG.error(exception);
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorPageView())
                .build();
    }
}

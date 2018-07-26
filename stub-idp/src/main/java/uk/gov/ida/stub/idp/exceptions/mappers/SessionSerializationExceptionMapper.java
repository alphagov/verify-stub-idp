package uk.gov.ida.stub.idp.exceptions.mappers;

import org.apache.log4j.Logger;
import uk.gov.ida.stub.idp.exceptions.SessionSerializationException;
import uk.gov.ida.stub.idp.views.ErrorPageView;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class SessionSerializationExceptionMapper implements ExceptionMapper<SessionSerializationException> {
	private static final Logger LOG = Logger.getLogger(SessionSerializationExceptionMapper.class);
	@Override
	public Response toResponse(SessionSerializationException exception) {
		LOG.error(exception);
		return Response
				.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorPageView())
				.build();
	}
}

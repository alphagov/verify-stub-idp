package uk.gov.ida.stub.idp.exceptions.mappers;

import uk.gov.ida.stub.idp.exceptions.SessionSerializationException;
import uk.gov.ida.stub.idp.views.ErrorPageView;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class SessionSerializationExceptionMapper implements ExceptionMapper<SessionSerializationException> {
	@Override
	public Response toResponse(SessionSerializationException exception) {
		return Response
				.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorPageView())
				.build();
	}
}

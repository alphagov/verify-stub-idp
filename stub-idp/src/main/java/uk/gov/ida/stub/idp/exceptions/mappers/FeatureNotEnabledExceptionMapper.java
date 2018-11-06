package uk.gov.ida.stub.idp.exceptions.mappers;

import org.apache.log4j.Logger;
import uk.gov.ida.stub.idp.exceptions.FeatureNotEnabledException;
import uk.gov.ida.stub.idp.exceptions.SessionSerializationException;
import uk.gov.ida.stub.idp.views.ErrorPageView;
import uk.gov.ida.stub.idp.views.FeatureNotEnabledPageView;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class FeatureNotEnabledExceptionMapper implements ExceptionMapper<FeatureNotEnabledException> {
	private static final Logger LOG = Logger.getLogger(FeatureNotEnabledExceptionMapper.class);
	@Override
	public Response toResponse(FeatureNotEnabledException exception) {
		LOG.error(exception);
		return Response
				.status(Response.Status.PRECONDITION_FAILED)
				.entity(new FeatureNotEnabledPageView())
				.build();
	}
}

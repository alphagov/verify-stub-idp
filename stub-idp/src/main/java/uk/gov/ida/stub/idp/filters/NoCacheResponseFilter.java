package uk.gov.ida.stub.idp.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;

import static uk.gov.ida.common.HttpHeaders.CACHE_CONTROL_KEY;
import static uk.gov.ida.common.HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE;
import static uk.gov.ida.common.HttpHeaders.PRAGMA_KEY;
import static uk.gov.ida.common.HttpHeaders.PRAGMA_NO_CACHE_VALUE;

public class NoCacheResponseFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        final MediaType responseContextMediaType = responseContext.getMediaType();

        if(MediaType.TEXT_HTML_TYPE.isCompatible(responseContextMediaType)
                || MediaType.APPLICATION_JSON_TYPE.isCompatible(responseContextMediaType)) {
            responseContext.getHeaders().add(CACHE_CONTROL_KEY, CACHE_CONTROL_NO_CACHE_VALUE);
            responseContext.getHeaders().add(PRAGMA_KEY, PRAGMA_NO_CACHE_VALUE);
        }
    }
}

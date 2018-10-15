package uk.gov.ida.stub.idp.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

public class SecurityHeadersFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().add("X-Frame-Options", "DENY");
        responseContext.getHeaders().add("X-XSS-Protection", "1; mode=block");
        responseContext.getHeaders().add("X-Content-Type-Options", "nosniff");
        final String contentSecurityPolicy = "default-src 'self'; " +
                "font-src 'data:'; " +
                "img-src 'self'; " +
                "object-src 'none'; " +
                "script-src 'self' 'unsafe-inline'; " + // would be nice to have digests of scripts here, but perhaps too much friction for a test app
                "style-src 'self' 'unsafe-inline'";
        responseContext.getHeaders().add("Content-Security-Policy", contentSecurityPolicy);
    }
}

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
                "font-src data:; " +
                "img-src 'self'; " +
                "object-src 'none'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "script-src 'self';";
        responseContext.getHeaders().add("Content-Security-Policy", contentSecurityPolicy);
    }
}

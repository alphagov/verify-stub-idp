package uk.gov.ida.filters;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Users browsing to the hub with Accept-Language codes which include
 * integers for instance "es-419 – Spanish appropriate for the Latin
 * America and Caribbean region". Are receiving 400 Bad Request from Jersey
 * as it fails to parse the header.
 *
 * This is resolved in the following issue upstream,
 *
 * https://java.net/jira/browse/JERSEY-2478
 *
 * Once Dropwizard releases a new stable version including this fixed
 * Jersey we should we remove this filter.
 *
 * We need to do this at the servlets level to intercept the header before
 * Jersey attempts to parse out the headers to build its
 * ContainerRequestContext.
 *
 * Followed some "recommendations" here:
 * http://stackoverflow.com/questions/2811769/adding-an-http-header-to-the-request-in-a-servlet-filter
 *
 * Not sure how to test this as our existing tests don't start the full environment?
 */
public class AcceptLanguageFilter implements Filter {
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }
    @Override
    public void doFilter(ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(new HideAcceptLanguage((HttpServletRequest) servletRequest), servletResponse);
    }

    @Override
    public void destroy() {
    }

    class HideAcceptLanguage extends HttpServletRequestWrapper {

        public HideAcceptLanguage(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getHeader(String name) {
            if (name.equals(HttpHeaders.ACCEPT_LANGUAGE)) {
                return null;
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            ArrayList<String> headers =  Collections.list(super.getHeaderNames());
            headers.remove(HttpHeaders.ACCEPT_LANGUAGE);
            return Collections.enumeration(headers);
        }
    }
}
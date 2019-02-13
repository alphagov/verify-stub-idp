package uk.gov.ida.restclient;

import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static java.text.MessageFormat.format;

public class TimeoutRequestRetryHandler implements HttpRequestRetryHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TimeoutRequestRetryHandler.class);
    private final int numRetries;

    public TimeoutRequestRetryHandler(int numRetries) {
        this.numRetries = numRetries;
    }

    @Override
    public boolean retryRequest(IOException e, int executionCount, HttpContext httpContext) {
        LOG.info("Retry request made.", e);
        if (e instanceof ConnectTimeoutException && executionCount <= numRetries) {
            String uri = null;
            String httpMethod = null;
            try {
                final HttpCoreContext coreContext = HttpCoreContext.adapt(httpContext);
                final HttpRequest request = coreContext.getRequest();
                final RequestLine requestLine = request.getRequestLine();
                uri = requestLine.getUri();
                httpMethod = requestLine.getMethod();
            }
            finally {
                LOG.info(format("Retrying {0} of {1}, to {2} / {3}", executionCount, numRetries, httpMethod, uri));
            }
            return true;
        }
        return false;
    }
}

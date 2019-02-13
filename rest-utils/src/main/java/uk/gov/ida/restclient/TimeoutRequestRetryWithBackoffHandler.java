package uk.gov.ida.restclient;

import io.dropwizard.util.Duration;
import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.text.MessageFormat.format;

public class TimeoutRequestRetryWithBackoffHandler implements HttpRequestRetryHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TimeoutRequestRetryHandler.class);
    private final int numRetries;
    private final Duration retryBackoffPeriod;
    private final List<Class> defaultRetryExceptions = Arrays.asList(new Class[]{ ConnectTimeoutException.class, SocketTimeoutException.class}) ;
    private List<Class> retryExceptions;

    public TimeoutRequestRetryWithBackoffHandler(int numRetries, Duration retryBackoffPeriod) {
        this.numRetries = numRetries;
        this.retryBackoffPeriod = retryBackoffPeriod;
        this.retryExceptions = defaultRetryExceptions;
    }

    public TimeoutRequestRetryWithBackoffHandler(int numRetries, Duration retryBackoffPeriod,List<String> retryExceptionNames) {
        this(numRetries, retryBackoffPeriod);
        if (retryExceptions != null) {
            this.retryExceptions = new ArrayList<>();
            for(String className : retryExceptionNames) {
                try {
                    this.retryExceptions.add(Class.forName(className));
                } catch(ClassNotFoundException e) {
                    LOG.error(format("Class {0} specified in exception class name list does not exist", className));
                }
            }
        }
    }

    @Override
    public boolean retryRequest(IOException e, int executionCount, HttpContext httpContext) {
        LOG.info("Retry request made.", e);
        if (retryExceptions.contains(e.getClass()) && executionCount <= numRetries) {
            String uri = null;
            String httpMethod = null;
            try {
                final HttpCoreContext coreContext = HttpCoreContext.adapt(httpContext);
                final HttpRequest request = coreContext.getRequest();
                final RequestLine requestLine = request.getRequestLine();
                uri = requestLine.getUri();
                httpMethod = requestLine.getMethod();
                LOG.info(format("Backing off for {0} milliseconds before retry", this.retryBackoffPeriod));
                Thread.sleep(executionCount * this.retryBackoffPeriod.toMilliseconds());
            } catch(InterruptedException ex) {
                LOG.error("Thread interrupted during backoff period");
            } finally {
                LOG.info(format("Retrying {0} of {1}, to {2} / {3}", executionCount, numRetries, httpMethod, uri));
            }
            return true;
        }
        return false;
    }
}

package uk.gov.ida.restclient;

import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TimeoutRequestRetryHandlerTest {

    private HttpContext httpContext = new BasicHttpContext();

    @Before
    public void setup() {
        httpContext.setAttribute(HttpClientContext.HTTP_REQUEST, new BasicHttpRequest("GET", "http://localhost"));
    }

    @Test
    public void should_retry_ConnectTimeoutException() {
        final int numRetries = 2;

        TimeoutRequestRetryHandler timeoutRequestRetryHandler = new TimeoutRequestRetryHandler(numRetries);
        final boolean expected = timeoutRequestRetryHandler.retryRequest(new ConnectTimeoutException(), 1, httpContext);

        assertTrue(expected);
    }

    @Test
    public void should_only_retry_set_number_of_times() {
        final int numRetries = 2;
        final int executionCount = 3;

        TimeoutRequestRetryHandler timeoutRequestRetryHandler = new TimeoutRequestRetryHandler(numRetries);
        boolean expected = timeoutRequestRetryHandler.retryRequest(new ConnectTimeoutException(), numRetries, httpContext);

        assertTrue(expected);

        expected = timeoutRequestRetryHandler.retryRequest(new ConnectTimeoutException(), executionCount, httpContext);

        assertFalse(expected);
    }

    @Test
    public void should_not_be_retry_other_exceptions() {
        final int numRetries = 2;

        TimeoutRequestRetryHandler timeoutRequestRetryHandler = new TimeoutRequestRetryHandler(numRetries);
        final boolean expected = timeoutRequestRetryHandler.retryRequest(new IOException(), 1, httpContext);

        assertFalse(expected);
    }

}

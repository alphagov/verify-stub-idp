package uk.gov.ida.restclient;

import io.dropwizard.util.Duration;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TimeoutRequestRetryWithBackoffHandlerTest {

    private HttpContext httpContext = new BasicHttpContext();

    @Before
    public void setup() {
        httpContext.setAttribute(HttpClientContext.HTTP_REQUEST, new BasicHttpRequest("GET", "http://localhost"));
    }

    @Test
    public void should_retry_ConnectTimeoutExceptionByDefault() {
        final int numRetries = 2;

        TimeoutRequestRetryWithBackoffHandler timeoutRequestRetryHandler = new TimeoutRequestRetryWithBackoffHandler(numRetries,Duration.milliseconds(1000));
        final boolean expected = timeoutRequestRetryHandler.retryRequest(new ConnectTimeoutException(), 1, httpContext);

        assertTrue(expected);
    }

    @Test
    public void should_retry_SocketTimeoutExceptionByDefault() {
        final int numRetries = 2;

        TimeoutRequestRetryWithBackoffHandler timeoutRequestRetryHandler = new TimeoutRequestRetryWithBackoffHandler(numRetries,Duration.milliseconds(1000));
        final boolean expected = timeoutRequestRetryHandler.retryRequest(new SocketTimeoutException(), 1, httpContext);

        assertTrue(expected);
    }

    @Test
    public void shouldRetryOnSpecifiedExceptionList() {
        final int numRetries = 2;
        final List<String> retryExceptionNames = Arrays.asList(new String[]{ "org.apache.http.conn.ConnectTimeoutException", "java.net.SocketTimeoutException", "org.apache.http.NoHttpResponseException"}) ;

        TimeoutRequestRetryWithBackoffHandler timeoutRequestRetryHandler = new TimeoutRequestRetryWithBackoffHandler(numRetries,Duration.milliseconds(1000),retryExceptionNames);
        boolean expected = timeoutRequestRetryHandler.retryRequest(new SocketTimeoutException(), 1, httpContext);
        assertTrue(expected);

        expected = timeoutRequestRetryHandler.retryRequest(new ConnectTimeoutException(), 1, httpContext);
        assertTrue(expected);

        expected = timeoutRequestRetryHandler.retryRequest(new NoHttpResponseException("Response is empty"), 1, httpContext);
        assertTrue(expected);

        expected = timeoutRequestRetryHandler.retryRequest(new IOException(), 1, httpContext);
        assertFalse(expected);
    }

    @Test
    public void should_only_retry_set_number_of_times() {
        final int numRetries = 2;
        final int executionCount = 3;

        TimeoutRequestRetryWithBackoffHandler timeoutRequestRetryHandler = new TimeoutRequestRetryWithBackoffHandler(numRetries,Duration.milliseconds(1000));
        boolean expected = timeoutRequestRetryHandler.retryRequest(new ConnectTimeoutException(), numRetries, httpContext);

        assertTrue(expected);


        timeoutRequestRetryHandler = new TimeoutRequestRetryWithBackoffHandler(numRetries,Duration.milliseconds(1000));
        expected = timeoutRequestRetryHandler.retryRequest(new ConnectTimeoutException(), executionCount, httpContext);

        assertFalse(expected);
    }

    @Test
    public void should_not_be_retry_other_exceptions() {
        final int numRetries = 2;

        TimeoutRequestRetryWithBackoffHandler timeoutRequestRetryHandler = new TimeoutRequestRetryWithBackoffHandler(numRetries,Duration.milliseconds(1000));
        final boolean expected = timeoutRequestRetryHandler.retryRequest(new IOException(), 1, httpContext);

        assertFalse(expected);
    }

    @Test
    public void firstRetryShouldBackOffForSpecifiedPeriod() {
        final int numRetries = 3;
        final Duration backOffPeriod = Duration.milliseconds(1000);
        final int retryAttempt = 1;

        TimeoutRequestRetryWithBackoffHandler timeoutRequestRetryHandler = new TimeoutRequestRetryWithBackoffHandler(numRetries,backOffPeriod);

        long start = System.currentTimeMillis();
        final boolean expected = timeoutRequestRetryHandler.retryRequest(new SocketTimeoutException(), retryAttempt, httpContext);
        long end = System.currentTimeMillis();

        assertTrue(expected);
        assertTrue((end-start) > retryAttempt * backOffPeriod.toMilliseconds());
    }

    @Test
    public void secondRetryShouldBackOffForTwiceSpecifiedPeriod() {
        final int numRetries = 3;
        final Duration backOffPeriod = Duration.milliseconds(1000);
        final int retryAttempt = 2;

        TimeoutRequestRetryWithBackoffHandler timeoutRequestRetryHandler = new TimeoutRequestRetryWithBackoffHandler(numRetries,backOffPeriod);

        long start = System.currentTimeMillis();
        final boolean expected = timeoutRequestRetryHandler.retryRequest(new SocketTimeoutException(), retryAttempt, httpContext);
        long end = System.currentTimeMillis();

        assertTrue(expected);
        assertTrue((end-start) > retryAttempt * backOffPeriod.toMilliseconds());
    }

    @Test
    public void thirdRetryShouldBackOffForThriceSpecifiedPeriod() {
        final int numRetries = 3;
        final Duration backOffPeriod = Duration.milliseconds(1000);
        final int retryAttempt = 3;

        TimeoutRequestRetryWithBackoffHandler timeoutRequestRetryHandler = new TimeoutRequestRetryWithBackoffHandler(numRetries,backOffPeriod);

        long start = System.currentTimeMillis();
        final boolean expected = timeoutRequestRetryHandler.retryRequest(new SocketTimeoutException(), retryAttempt, httpContext);
        long end = System.currentTimeMillis();

        assertTrue(expected);
        assertTrue((end-start) > retryAttempt * backOffPeriod.toMilliseconds());
    }
}

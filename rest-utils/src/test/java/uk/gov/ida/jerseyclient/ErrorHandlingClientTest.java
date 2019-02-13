package uk.gov.ida.jerseyclient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.exceptions.ApplicationException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ErrorHandlingClientTest {

    @Mock
    private Client client;

    @Mock
    private WebTarget webTarget;

    @Mock
    private Invocation.Builder webTargetBuilder;

    private ErrorHandlingClient errorHandlingClient;

    private URI testUri;

    @Before
    public void setup() {
        errorHandlingClient = new ErrorHandlingClient(client);
        when(client.target(any(URI.class))).thenReturn(webTarget);
        when(webTarget.request()).thenReturn(webTargetBuilder);
        when(webTarget.request(MediaType.APPLICATION_JSON_TYPE)).thenReturn(webTargetBuilder);
        when(webTargetBuilder.accept(Matchers.<MediaType>any())).thenReturn(webTargetBuilder);
        when(webTargetBuilder.cookie(Matchers.<Cookie>any())).thenReturn(webTargetBuilder);
        when(webTargetBuilder.header(anyString(), Matchers.any())).thenReturn(webTargetBuilder);

        testUri = URI.create("/some-uri");
    }

    @Test
    public void getWithCookiesAndHeaders_shouldAddCookiesAndHeadersToRequest() throws Exception {
        final Cookie cookie = new Cookie("cookie", "monster");
        final List<Cookie> cookies = ImmutableList.of(cookie);
        final String headerName = "X-Clacks-Overhead";
        final String headerValue = "GNU Terry Pratchett";
        final Map<String, String> headers = ImmutableMap.of(headerName, headerValue);

        errorHandlingClient.get(testUri, cookies, headers);

        verify(webTargetBuilder, times(1)).cookie(cookie);
        verify(webTargetBuilder, times(1)).header(headerName, headerValue);
        verify(webTargetBuilder, times(1)).get();
    }

    @Test(expected = ApplicationException.class)
    public void get_shouldThrowApplicationExceptionWhenAWireProblemOccurs() throws Exception {
        when(client.target(testUri)).thenThrow(new ProcessingException(""));

        errorHandlingClient.get(testUri);
    }

    @Test
    public void postWithHeaders_shouldAddHeadersToRequest() throws Exception {
        final String headerName = "X-Clacks-Overhead";
        final String headerValue = "GNU Terry Pratchett";
        final Map<String, String> headers = ImmutableMap.of(headerName, headerValue);

        final String postBody = "";
        errorHandlingClient.post(testUri, headers, postBody);

        verify(webTargetBuilder, times(1)).header(headerName, headerValue);
        verify(webTargetBuilder, times(1)).post(Entity.json(postBody));
    }

    @Test(expected = ApplicationException.class)
    public void shouldRetryPostRequestIfConfigured() throws Exception {
        when(webTargetBuilder.post(Entity.json(""))).thenThrow(RuntimeException.class);

        ErrorHandlingClient retryEnabledErrorHandlingClient = new ErrorHandlingClient(client, 2);
        retryEnabledErrorHandlingClient.post(testUri, Collections.emptyMap(), "");

        verify(webTargetBuilder, times(2)).post(Entity.json(""));
    }

    @Test(expected = ApplicationException.class)
    public void shouldRetryGetRequestIfConfigured() throws Exception {
        when(webTargetBuilder.get()).thenThrow(RuntimeException.class);

        ErrorHandlingClient retryEnabledErrorHandlingClient = new ErrorHandlingClient(client, 2);
        retryEnabledErrorHandlingClient.get(testUri);

        verify(webTargetBuilder, times(2)).get();
    }

    @Test(expected = ApplicationException.class)
    public void post_shouldThrowApplicationExceptionWhenAWireProblemOccurs() throws Exception {
        when(client.target(testUri)).thenThrow(new ProcessingException(""));

        errorHandlingClient.post(testUri, "");
    }
}

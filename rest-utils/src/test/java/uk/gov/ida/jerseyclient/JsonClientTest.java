package uk.gov.ida.jerseyclient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JsonClientTest {

    @Mock
    private ErrorHandlingClient errorHandlingClient;
    @Mock
    private JsonResponseProcessor jsonResponseProcessor;

    private JsonClient jsonClient;
    private URI testUri = URI.create("/some-uri");
    private String requestBody = "some-request-body";

    @Before
    public void setup() {
        jsonClient = new JsonClient(errorHandlingClient, jsonResponseProcessor);
    }

    @Test
    public void post_shouldDelegateToJsonResponseProcessorToCheckForErrors() throws Exception {
        Response clientResponse = Response.noContent().build();
        when(errorHandlingClient.post(testUri, requestBody)).thenReturn(clientResponse);

        jsonClient.post(requestBody, testUri);

        verify(jsonResponseProcessor, times(1)).getJsonEntity(testUri, null, null, clientResponse);
    }

    @Test
    public void basicPost_shouldDelegateToProcessor() throws Exception {
        Response clientResponse = Response.noContent().build();
        when(errorHandlingClient.post(testUri, requestBody)).thenReturn(clientResponse);

        jsonClient.post(requestBody, testUri, String.class);

        verify(jsonResponseProcessor, times(1)).getJsonEntity(testUri, null, String.class, clientResponse);
    }

    @Test
    public void basicGet_shouldDelegateToProcessor() throws Exception {
        Response clientResponse = Response.noContent().build();
        when(errorHandlingClient.get(testUri)).thenReturn(clientResponse);

        jsonClient.get(testUri, String.class);

        verify(jsonResponseProcessor, times(1)).getJsonEntity(testUri, null, String.class, clientResponse);
    }

    @Test
    public void getWithGenericType_shouldDelegateToProcessor() throws Exception {
        Response clientResponse = Response.noContent().build();
        when(errorHandlingClient.get(testUri)).thenReturn(clientResponse);
        GenericType<String> genericType = new GenericType<String>() {};

        jsonClient.get(testUri, genericType);

        verify(jsonResponseProcessor, times(1)).getJsonEntity(testUri, genericType, null, clientResponse);
    }
}

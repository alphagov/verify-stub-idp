package uk.gov.ida.jerseyclient;

import javax.inject.Inject;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class JsonClient {

    private final JsonResponseProcessor responseProcessor;
    private final ErrorHandlingClient errorHandlingClient;

    @Inject
    public JsonClient(ErrorHandlingClient errorHandlingClient, JsonResponseProcessor responseProcessor) {
        this.errorHandlingClient = errorHandlingClient;
        this.responseProcessor = responseProcessor;
    }

    public <T> T post(Object postBody, URI uri, Class<T> clazz) {
        return responseProcessor.getJsonEntity(uri, null, clazz, errorHandlingClient.post(uri, postBody));
    }

    public <T> T post(Object postBody, URI uri, Class<T> clazz, Map<String, String> headers) {
        return responseProcessor.getJsonEntity(uri, null, clazz, errorHandlingClient.post(uri, headers, postBody));
    }

    public void post(Object postBody, URI uri) {
        responseProcessor.getJsonEntity(uri, null, null, errorHandlingClient.post(uri, postBody));
    }

    public <T> T get(URI uri, Class<T> clazz) {
        return responseProcessor.getJsonEntity(uri, null, clazz, errorHandlingClient.get(uri));
    }

    public <T> T get(URI uri, Class<T> clazz, List<Cookie> cookies, Map<String, String> headers) {
        return responseProcessor.getJsonEntity(uri, null, clazz, errorHandlingClient.get(uri, cookies, headers));
    }

    public <T> T get(URI uri, GenericType<T> genericType) {
        return responseProcessor.getJsonEntity(uri, genericType, null, errorHandlingClient.get(uri));
    }
}

package uk.gov.ida.jerseyclient;

import uk.gov.ida.common.ExceptionType;

import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static uk.gov.ida.exceptions.ApplicationException.createUnauditedException;

public class ErrorHandlingClient {

    private final Client jerseyClient;
    private Integer numberOfRetries = 0;

    @Inject
    public ErrorHandlingClient(Client jerseyClient) {
        numberOfRetries = 0;
        this.jerseyClient = jerseyClient;
    }

    public ErrorHandlingClient(Client jerseyClient, Integer numberOfRetries) {
        this.numberOfRetries = numberOfRetries;
        this.jerseyClient = jerseyClient;
    }

    public Response get(final URI uri) {
        return get(uri, Collections.<Cookie>emptyList(), Collections.<String, String>emptyMap());
    }

    public Response get(final URI uri, final List<Cookie> cookies, final Map<String, String> headers) {
        try {
            Invocation.Builder requestBuilder = jerseyClient.target(uri).request();
            for (Cookie cookie : cookies) {
                requestBuilder = requestBuilder.cookie(cookie);
            }
            requestBuilder = addHeaders(headers, requestBuilder);

            Invocation.Builder client = requestBuilder.accept(MediaType.APPLICATION_JSON_TYPE);

            if (numberOfRetries!=0){
                RetryCommand<Response> retryCommand = new RetryCommand<>(numberOfRetries);
                return retryCommand.execute(client::get);
            }else {
               return client.get();
            }

        } catch (ProcessingException e) {
            throw createUnauditedException(ExceptionType.NETWORK_ERROR, UUID.randomUUID(), e, uri);
        }
    }

    public Response post(final URI uri, final Object postBody) {
        return post(uri, Collections.<String, String>emptyMap(), postBody);
    }

    public Response post(final URI uri, final Map<String, String> headers, final Object postBody) {
        try {
            Invocation.Builder request = jerseyClient.target(uri).request(MediaType.APPLICATION_JSON_TYPE);
            final Invocation.Builder requestBuilder = addHeaders(headers, request);

            if (numberOfRetries!=0){
                RetryCommand<Response> retryCommand = new RetryCommand<>(numberOfRetries);
                return retryCommand.execute(() -> requestBuilder.post(Entity.json(postBody)));
            }else {
                return requestBuilder.post(Entity.json(postBody));
            }

        } catch (ProcessingException e) {
            throw createUnauditedException(ExceptionType.NETWORK_ERROR, UUID.randomUUID(), e, uri);
        }
    }

    private Invocation.Builder addHeaders(Map<String, String> headers, Invocation.Builder requestBuilder) {
        for(Map.Entry<String, String> headerDetail: headers.entrySet()){
            if(headerDetail.getValue() != null) {
                requestBuilder = requestBuilder.header(headerDetail.getKey(), headerDetail.getValue());
            }
        }
        return requestBuilder;
    }
}

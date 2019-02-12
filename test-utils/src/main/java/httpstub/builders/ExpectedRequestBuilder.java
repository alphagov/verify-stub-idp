package httpstub.builders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMultimap;
import httpstub.ExpectedRequest;
import io.dropwizard.jackson.Jackson;

public class ExpectedRequestBuilder {
    private String path;
    private String method;
    private ImmutableMultimap<String, String> headers;
    private String body;

    private ObjectMapper objectMapper = Jackson.newObjectMapper();

    private ExpectedRequestBuilder() {
    }

    public ExpectedRequestBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static ExpectedRequestBuilder expectRequest() {
       return new ExpectedRequestBuilder();
    }

    public static ExpectedRequestBuilder expectRequest(ObjectMapper objectMapper) {
        return new ExpectedRequestBuilder(objectMapper);
    }

    public ExpectedRequestBuilder withPath(String path) {
        this.path = path;
        return this;
    }

    public ExpectedRequestBuilder withMethod(String method) {
        this.method = method;
        return this;
    }

    public ExpectedRequestBuilder withHeaders(ImmutableMultimap<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public ExpectedRequestBuilder withBody(String body) {
        this.body = body;
        return this;
    }

    public ExpectedRequestBuilder withBody(Object body) throws JsonProcessingException {
        this.body = objectMapper.writeValueAsString(body);
        return this;
    }

    public ExpectedRequest build() {
        return new ExpectedRequest(path, method, headers, body);
    }

    public RequestAndResponseBuilder andWillRespondWith() {
        return new RequestAndResponseBuilder(this);
    }
}
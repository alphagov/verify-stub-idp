package httpstub.builders;

import com.fasterxml.jackson.core.JsonProcessingException;
import httpstub.RequestAndResponse;

import java.util.Map;

public class RequestAndResponseBuilder {
    private ExpectedRequestBuilder expectedRequestBuilder;
    private RegisteredResponseBuilder registeredResponseBuilder = new RegisteredResponseBuilder();

    RequestAndResponseBuilder(ExpectedRequestBuilder expectedRequestBuilder) {
        this.expectedRequestBuilder= expectedRequestBuilder;
    }

    public RequestAndResponse build() {
        return new RequestAndResponse(expectedRequestBuilder.build(), registeredResponseBuilder.build());
    }

    public RequestAndResponseBuilder withStatus(int status) {
        registeredResponseBuilder.withStatus(status);
        return this;
    }

    public RequestAndResponseBuilder withContentType(String contentType) {
        registeredResponseBuilder.withContentType(contentType);
        return this;
    }


    public RequestAndResponseBuilder withBody(String body) {
        registeredResponseBuilder.withBody(body);
        return this;
    }

    public RequestAndResponseBuilder withBody(Object body) throws JsonProcessingException {
        registeredResponseBuilder.withBody(body);
        return this;
    }

    public RequestAndResponseBuilder withHeaders(Map<String, String> headers) {
        registeredResponseBuilder.withHeaders(headers);
        return this;
    }
}

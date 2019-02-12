package httpstub.builders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import httpstub.RegisteredResponse;
import io.dropwizard.jackson.Jackson;

import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Map;

public class RegisteredResponseBuilder {
    private final ObjectMapper objectMapper;

    private int status = 200;
    private String contentType = MediaType.APPLICATION_JSON;
    private String body = "";
    private Map<String, String> headers = Collections.emptyMap();

    public RegisteredResponseBuilder() {
        this.objectMapper = Jackson.newObjectMapper();
    }

    public static RegisteredResponseBuilder aRegisteredResponse() {
        return new RegisteredResponseBuilder();
    }

    public RegisteredResponseBuilder withStatus(int status) {
        this.status = status;
        return this;
    }

    public RegisteredResponseBuilder withContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }


    public RegisteredResponseBuilder withBody(String body) {
        this.body = body;
        return this;
    }

    public RegisteredResponseBuilder withBody(Object body) throws JsonProcessingException {
        this.body = objectMapper.writeValueAsString(body);
        return this;
    }

    public RegisteredResponseBuilder withHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public RegisteredResponse build() {
        return new RegisteredResponse(status, contentType, body, headers);
    }
}

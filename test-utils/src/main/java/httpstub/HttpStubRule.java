package httpstub;

import com.fasterxml.jackson.core.JsonProcessingException;
import httpstub.builders.ExpectedRequestBuilder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;

import static httpstub.builders.ExpectedRequestBuilder.expectRequest;

public class HttpStubRule implements TestRule {

    private final AbstractHttpStub httpStub;

    public HttpStubRule() {
        this(new HttpStub());
    }

    protected HttpStubRule(AbstractHttpStub httpStub) {
        this.httpStub = httpStub;
        this.httpStub.start();
    }

    public int getPort() {
        return httpStub.getHttpPort();
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } finally {
                    httpStub.stop();
                }
            }
        };
    }

    public URI uri(String path) {
        return baseUri().path(path).build();
    }

    public UriBuilder baseUri() {
        return UriBuilder.fromUri("http://localhost").port(getPort());
    }

    public void register(String path, int responseStatus) {
        RequestAndResponse requestAndResponse = expectRequest().withPath(path).andWillRespondWith().withStatus(responseStatus).build();
        register(requestAndResponse);
    }

    public void register(String path, int responseStatus, String contentType, String responseBody) throws JsonProcessingException {
        RequestAndResponse requestAndResponse = expectRequest().withPath(path).andWillRespondWith().withStatus(responseStatus).withContentType(contentType).withBody(responseBody).build();
        register(requestAndResponse);
    }

    public void register(String path, int status, Object responseEntity) throws JsonProcessingException {
        RequestAndResponse requestAndResponse = expectRequest().withPath(path).andWillRespondWith().withStatus(status).withBody(responseEntity).build();
        register(requestAndResponse);
    }

    public void register(String path, RegisteredResponse registeredResponse) {
        register(ExpectedRequestBuilder.expectRequest().withPath(path).build(), registeredResponse);
    }

    public void register(ExpectedRequest expectedRequest, RegisteredResponse registeredResponse) {
        RequestAndResponse requestAndResponse = new RequestAndResponse(expectedRequest, registeredResponse);
        register(requestAndResponse);
    }

    public void register(RequestAndResponse requestAndResponse) {
        httpStub.register(requestAndResponse);
    }

    public void reset() {
        httpStub.reset();
    }

    public int getCountOfRequestsTo(final String path) {
        return httpStub.getCountOfRequestsTo(path);
    }

    public int getCountOfRequests() {
        return httpStub.getCountOfRequests();
    }

    public RecordedRequest getLastRequest() {
        return httpStub.getLastRequest();
    }

    public List<RecordedRequest> getRecordedRequest() {
       return httpStub.getRecordedRequests();
    }
}

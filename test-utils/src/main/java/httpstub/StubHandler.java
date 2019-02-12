package httpstub;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Throwables.propagate;

public abstract class StubHandler extends AbstractHandler {
    protected void respondWith(Request baseRequest, HttpServletResponse response, RegisteredResponse registeredResponse) {
        response.setStatus(registeredResponse.getStatus());
        for (Map.Entry<String, String> headerEntry : registeredResponse.getHeaders().entrySet()) {
            response.setHeader(headerEntry.getKey(), headerEntry.getValue());
        }
        response.setContentType(registeredResponse.getContentType());
        try {
            response.getWriter().append(registeredResponse.getBody());
        } catch (IOException e) {
            throw propagate(e);
        }
        baseRequest.setHandled(true);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response){
        RecordedRequest recordedRequest = new RecordedRequest(baseRequest);
        recordRequest(recordedRequest);
        findResponse(recordedRequest).ifPresent(requestAndResponse -> {
            requestAndResponse.addCall();
            respondWith(baseRequest, response, requestAndResponse.getResponse());
        });
    }

    protected abstract Optional<RequestAndResponse> findResponse(ReceivedRequest recordedRequest);

    protected abstract void recordRequest(RecordedRequest recordedRequest);
}

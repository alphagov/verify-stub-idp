package httpstub;

import javax.validation.constraints.NotNull;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestAndResponse {
    private ExpectedRequest request;
    private RegisteredResponse response;
    private AtomicInteger callCount = new AtomicInteger(0);

    public RequestAndResponse(@NotNull ExpectedRequest request, @NotNull RegisteredResponse response) {
        this.request = request;
        this.response = response;
    }

    public ExpectedRequest getRequest() {
        return request;
    }

    public RegisteredResponse getResponse() {
        return response;
    }

    public void addCall() {
        callCount.incrementAndGet();
    }

    public int callCount() {
        return callCount.get();
    }
}

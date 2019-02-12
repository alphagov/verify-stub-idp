package httpstub;

import java.util.Optional;

/**
 * With StackedHttpStub registered responses are only used once and then discarded;
 * multiple different responses can be queued up to be consumed
 *
 * To use this create a class such as this:
 *
 *   public class MyHttpStub extends HttpStubRule {
 *     public MyHttpStub() {
 *       super(new StackedHttpStub());
 *     }
 *   }
 *
 */
public class StackedHttpStub extends AbstractHttpStub {
    public StackedHttpStub() {
        this(RANDOM_PORT);
    }

    public StackedHttpStub(int port) {
        super(port);
    }

    @Override
    public StubHandler createHandler() {
        return new Handler();
    }

    private class Handler extends StubHandler {
        @Override
        protected Optional<RequestAndResponse> findResponse(ReceivedRequest receivedRequest) {
            return requestsAndResponses
                    .stream()
                    .filter(requestAndResponse -> requestAndResponse.getRequest().applies(receivedRequest))
                    .filter(requestAndResponse -> requestAndResponse.callCount() == 0)
                    .findFirst();
        }

        @Override
        protected void recordRequest(RecordedRequest recordedRequest) {
            recordedRequests.add(recordedRequest);
        }
    }
}

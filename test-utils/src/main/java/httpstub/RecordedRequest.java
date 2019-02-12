package httpstub;

import org.eclipse.jetty.server.Request;

public class RecordedRequest extends ReceivedRequest {
    public RecordedRequest(Request request) {
        super(request);
    }
}

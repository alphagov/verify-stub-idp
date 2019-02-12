package httpstub;

import com.google.common.collect.ImmutableMultimap;

import javax.annotation.Nullable;
import java.util.Map;

public class ExpectedRequest {
    private final String path;
    private final String method;
    private ImmutableMultimap<String, String> headers;
    private String body;

    public ExpectedRequest(@Nullable String path, @Nullable String method, @Nullable ImmutableMultimap<String, String> headers, @Nullable String body) {
        this.path = path;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    public boolean applies(ReceivedRequest baseRequest) {
        if(path != null && !baseRequest.getPath().equals(path)) {
            return false;
        }
        if(method != null && !baseRequest.getMethod().equals(method)) {
            return false;
        }
        if(headers != null && notAllHeadersFound(baseRequest.getHeaders())) {
            return false;
        }
        if(body != null && !baseRequest.getEntity().equals(body)) {
            return false;
        }
        return true;
    }

    private boolean notAllHeadersFound(ImmutableMultimap<String, String> headers) {
        return !this.headers.entries().stream().allMatch(headerEntry -> isHeaderInRequest(headers, headerEntry));
    }

    private boolean isHeaderInRequest(ImmutableMultimap<String, String> receivedHeaders, Map.Entry<String, String> expectedEntry) {
        return receivedHeaders.containsEntry(expectedEntry.getKey(), expectedEntry.getValue());
    }
}

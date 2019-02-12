package httpstub;

import java.util.Map;

public class RegisteredResponse {
    private final int status;
    private final String contentType;
    private final String body;
    private final Map<String, String> headers;

    public RegisteredResponse(int status, String contentType, String body, Map<String, String> headers) {
        this.status = status;
        this.contentType = contentType;
        this.body = body;
        this.headers = headers;
    }

    public int getStatus() {
        return status;
    }

    public String getContentType() {
        return contentType;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

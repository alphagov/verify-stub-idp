package uk.gov.ida.eventemitter.utils;

public enum HttpResponse {

    HTTP_200(200, "OK"),
    HTTP_403(403, "Forbidden"),
    HTTP_404(404, "Not Found"),
    HTTP_504(504, "Gateway Timeout");

    private int statusCode;
    private String statusText;

    HttpResponse(int statusCode, String statusText) {
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

}

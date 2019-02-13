package uk.gov.ida.eventemitter;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.http.HttpResponse;

public class AwsResponseException extends AmazonServiceException {
    private HttpResponse response;

    public AwsResponseException(HttpResponse response) {
        super(response.getStatusText());
        this.setStatusCode(response.getStatusCode());
        this.setServiceName("API Gateway");
        this.response = response;
    }

    public HttpResponse getResponse() {
        return response;
    }
}

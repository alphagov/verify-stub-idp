package uk.gov.ida.eventemitter;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.regions.Regions;
import com.amazonaws.util.StringInputStream;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;


public class AmazonEventSender implements EventSender {

    private final static String SERVICE_NAME = "execute-api";

    private URI apiGatewayUrl;
    private AWSCredentials credentials;
    private Regions region;

    public AmazonEventSender(final URI apiGatewayUrl, final AWSCredentials credentials, final Regions region) {
        this.apiGatewayUrl = apiGatewayUrl;
        this.credentials = credentials;
        this.region = region;
    }

    public void sendAuthenticated(final Event event, final String encryptedEvent) throws AwsResponseException, UnsupportedEncodingException {

        Request<Void> request = createRequest(encryptedEvent);
        request = signRequest(request);

        new AmazonHttpClient(new ClientConfiguration())
                .requestExecutionBuilder()
                .executionContext(new ExecutionContext(true))
                .request(request)
                .errorResponseHandler(new HttpResponseHandler<AwsResponseException>() {
                    @Override
                    public AwsResponseException handle(HttpResponse response) {
                        return new AwsResponseException(response);
                    }

                    @Override
                    public boolean needsConnectionLeftOpen() {
                        return false;
                    }
                })
                .execute();
    }

    private Request<Void> signRequest(Request<Void> request) {
        AWS4Signer signer = new AWS4Signer();
        signer.setRegionName(region.getName());
        signer.setServiceName(request.getServiceName());
        signer.sign(request, credentials);
        return request;
    }

    private Request<Void> createRequest(String encryptedEvent) throws UnsupportedEncodingException {

        Request<Void> request = new DefaultRequest<>(SERVICE_NAME);
        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put("Content-type", "application/json");
        request.setHeaders(headersMap);
        request.setHttpMethod(HttpMethodName.POST);
        request.setEndpoint(apiGatewayUrl);
        request.setContent(new StringInputStream(encryptedEvent));
        return request;
    }
}

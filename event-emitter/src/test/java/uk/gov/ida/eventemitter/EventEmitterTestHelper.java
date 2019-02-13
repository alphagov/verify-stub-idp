package uk.gov.ida.eventemitter;

import com.amazonaws.regions.Regions;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.util.Modules;
import uk.gov.ida.eventemitter.utils.TestConfiguration;
import uk.gov.ida.eventemitter.utils.TestEventEmitterModule;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class EventEmitterTestHelper {

    protected static final String ACCESS_KEY_ID = "accessKeyId";
    protected static final String ACCESS_SECRET_KEY = "accessSecretKey";
    protected static final byte[] KEY = "aesEncryptionKey".getBytes();
    protected static final String AUDIT_EVENTS_API_RESOURCE = "/v1/auditevents/";
    protected static final String AUDIT_EVENTS_API_RESOURCE_INVALID = "/1234";

    protected static Injector injector;

    public static Injector createTestConfiguration(
            Boolean isEnabled,
            String accessKey,
            String secretAccessKey,
            Regions region,
            URI uri
    ) {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
            }

            @Provides
            @Singleton
            private Configuration getConfiguration() {
                return new TestConfiguration(
                        isEnabled,
                        accessKey,
                        secretAccessKey,
                        region,
                        uri,
                        KEY
                );
            }
        }, Modules.override(new EventEmitterModule()).with(new TestEventEmitterModule()));

    }

    public static Map<String, String> createTestResponseHeadersMap() {
        final Map<String, String> responseHeaders = new HashMap<String, String>();
        responseHeaders.put("Content-Length", "1");
        responseHeaders.put("Connection", "2");
        responseHeaders.put("x-amzn-RequestId", "3");
        responseHeaders.put("x-amzn-ErrorType", "4");
        responseHeaders.put("x-amz-apigw-id", "5");
        return responseHeaders;
    }
}

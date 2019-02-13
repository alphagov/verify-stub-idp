package uk.gov.ida.common;

public interface HttpHeaders {
    String CACHE_CONTROL_KEY = org.apache.http.HttpHeaders.CACHE_CONTROL; //"Cache-Control"
    String CACHE_CONTROL_NO_CACHE_VALUE = "no-cache, no-store";
    String PRAGMA_KEY = org.apache.http.HttpHeaders.PRAGMA;
    String PRAGMA_NO_CACHE_VALUE = "no-cache";
    String MAX_AGE = "max-age";
    String REFERER = com.google.common.net.HttpHeaders.REFERER;
    String X_FORWARDED_FOR = com.google.common.net.HttpHeaders.X_FORWARDED_FOR;
}

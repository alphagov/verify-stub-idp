package uk.gov.ida.dropwizard.logstash.support;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessEventFormat {
    public static class Bytes {
        public int bytes;
    }
    public static class Milliseconds {
        public int ms;
    }

    @JsonProperty("@timestamp")
    public String timestamp;

    @JsonProperty("@version")
    public int version;

    public String message;

    public AccessData access;

    public static class AccessData {
        public Bytes body_sent;
        public Milliseconds elapsed_time;
        public String method;
        public String http_version;
        public String remote_ip;
        public String user_name;
        public int response_code;
        public String url;
        public String referrer;
        public String agent;
        public String host;
    }

    private AccessEventFormat() {

    }

    public AccessEventFormat(String timestamp, int version) {
        this.timestamp = timestamp;
        this.version = version;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getVersion() {
        return version;
    }

    public int getBytesSent() {
        return access.body_sent.bytes;
    }

    public String getReferer() {
        return access.referrer;
    }

    public String getUserAgent() {
        return access.agent;
    }

    public String getHost() {
        return access.host;
    }

    public int getElapsedTimeMillis() {
        return access.elapsed_time.ms;
    }

    public String getMethod() {
        return access.method;
    }

    public String getHttpVersion() {
        return access.http_version;
    }

    public String getRemoteIp() {
        return access.remote_ip;
    }

    public String getUserName() {
        return access.user_name;
    }

    public int getResponseCode() {
        return access.response_code;
    }

    public String getUrl() {
        return access.url;
    }

    public String getMessage() {
        return message;
    }
}

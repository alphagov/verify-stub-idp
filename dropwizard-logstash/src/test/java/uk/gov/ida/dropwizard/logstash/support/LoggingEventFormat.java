package uk.gov.ida.dropwizard.logstash.support;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoggingEventFormat {

    @JsonProperty("@timestamp")
    public String timestamp;

    @JsonProperty("@version")
    public String version;

    public String message;

    public String logger_name;

    public String thread_name;

    public String level;

    public String level_value;

    public String tags;

    private LoggingEventFormat() {

    }

    public LoggingEventFormat(String timestamp, String version, String message, String logger_name, String thread_name, String level, String level_value, String tags) {
        this.timestamp = timestamp;
        this.version = version;
        this.message = message;
        this.logger_name = logger_name;
        this.thread_name = thread_name;
        this.level = level;
        this.level_value = level_value;
        this.tags = tags;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getVersion() {
        return version;
    }

    public String getMessage() {
        return message;
    }

    public String getLoggerName() {
        return logger_name;
    }

    public String getThreadName() {
        return thread_name;
    }

    public String getLevel() {
        return level;
    }

    public String getLevelValue() {
        return level_value;
    }

    public String getTags() {
        return tags;
    }

}

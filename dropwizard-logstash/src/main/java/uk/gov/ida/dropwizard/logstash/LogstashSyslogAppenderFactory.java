package uk.gov.ida.dropwizard.logstash;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.net.SyslogConstants;
import ch.qos.logback.core.net.SyslogOutputStream;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.AbstractAppenderFactory;
import io.dropwizard.logging.SyslogAppenderFactory;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;
import net.logstash.logback.layout.LogstashLayout;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@JsonTypeName("logstash-syslog")
public class LogstashSyslogAppenderFactory extends AbstractAppenderFactory<ILoggingEvent> {

    @NotNull
    @JsonProperty
    private String host = "localhost";

    @Min(1)
    @Max(65535)
    @JsonProperty
    private int port = SyslogConstants.SYSLOG_PORT;

    @NotNull
    @JsonProperty
    private SyslogAppenderFactory.Facility facility = SyslogAppenderFactory.Facility.LOCAL1;

    @NotNull
    @JsonProperty
    public String tag;

    @Override
    public Appender<ILoggingEvent> build(
            LoggerContext context,
            String applicationName,
            LayoutFactory<ILoggingEvent> layout,
            LevelFilterFactory<ILoggingEvent> levelFilterFactory,
            AsyncAppenderFactory<ILoggingEvent> asyncAppenderFactory) {

        String hostname = getLocalHostname();
        LogstashLayout logstashLayout = createLogstashLayout(context);
        logstashLayout.start();
        SyslogEventFormatter eventFormatter = new SyslogEventFormatter(facility, hostname, tag, logstashLayout);
        SyslogOutputStream outputStream = createSyslogOutputStream();

        SyslogAppender appender = createAppender(eventFormatter, outputStream, context);
        appender.start();
        return wrapAsync(appender, asyncAppenderFactory);
    }

    private SyslogAppender createAppender(SyslogEventFormatter eventFormatter, SyslogOutputStream outputStream, LoggerContext context) {
        final SyslogAppender appender = new SyslogAppender(eventFormatter, outputStream);
        appender.setName("logstash-syslog-appender");
        appender.setContext(context);
        final ThresholdFilter filter = new ThresholdFilter();
        filter.setLevel(threshold.toString());
        filter.start();
        appender.addFilter(filter);
        return appender;
    }

    private LogstashLayout createLogstashLayout(Context context) {
        LogstashLayout logstashLayout = new LogstashLayout();
        logstashLayout.setContext(context);
        logstashLayout.setIncludeCallerData(false);
        return logstashLayout;
    }

    private SyslogOutputStream createSyslogOutputStream() {
        try {
            return new SyslogOutputStream(host, port);
        } catch (UnknownHostException | SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLocalHostname() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}

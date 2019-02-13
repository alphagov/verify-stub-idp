package uk.gov.ida.dropwizard.logstash;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.Encoder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.FileAppenderFactory;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;
import net.logstash.logback.encoder.LogstashEncoder;

@JsonTypeName("logstash-file")
public class LogstashFileAppenderFactory extends FileAppenderFactory<ILoggingEvent> {
    @Override
    public Appender<ILoggingEvent> build(LoggerContext context,
                                         String applicationName,
                                         LayoutFactory<ILoggingEvent> layout,
                                         LevelFilterFactory<ILoggingEvent> levelFilterFactory,
                                         AsyncAppenderFactory<ILoggingEvent> asyncAppenderFactory) {
        Encoder<ILoggingEvent> encoder = new LogstashEncoder();
        encoder.setContext(context);
        encoder.start();

        final FileAppender<ILoggingEvent> appender = buildAppender(context);
        appender.setName("logstash-file-appender");

        appender.setAppend(true);
        appender.setContext(context);
        appender.setEncoder(encoder);
        appender.setFile(getCurrentLogFilename());
        appender.setPrudent(false);
        final ThresholdFilter filter = new ThresholdFilter();
        filter.setLevel(threshold.toString());
        filter.start();
        appender.addFilter(filter);
        appender.start();

        return wrapAsync(appender, asyncAppenderFactory);
    }
}

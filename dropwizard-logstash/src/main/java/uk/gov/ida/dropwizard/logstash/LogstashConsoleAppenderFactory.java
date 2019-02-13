package uk.gov.ida.dropwizard.logstash;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.Encoder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.ConsoleAppenderFactory;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;
import net.logstash.logback.encoder.LogstashEncoder;

@JsonTypeName("logstash-console")
public class LogstashConsoleAppenderFactory extends ConsoleAppenderFactory<ILoggingEvent> {

    @Override
    public Appender<ILoggingEvent> build(LoggerContext context,
                                         String applicationName,
                                         LayoutFactory<ILoggingEvent> layout,
                                         LevelFilterFactory<ILoggingEvent> levelFilterFactory,
                                         AsyncAppenderFactory<ILoggingEvent> asyncAppenderFactory) {

        Encoder<ILoggingEvent> encoder = new LogstashEncoder();
        encoder.setContext(context);
        encoder.start();

        final ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        appender.setName("logstash-console-appender");
        appender.setContext(context);
        appender.setTarget(getTarget().get());
        appender.setEncoder(encoder);
        final ThresholdFilter filter = new ThresholdFilter();
        filter.setLevel(threshold.toString());
        filter.start();
        appender.addFilter(filter);
        appender.start();

        return wrapAsync(appender, asyncAppenderFactory);
    }
}

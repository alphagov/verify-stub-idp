package uk.gov.ida.dropwizard.logstash;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.net.SyslogOutputStream;

import java.io.IOException;

public class SyslogAppender extends AppenderBase<ILoggingEvent> {
    private final SyslogEventFormatter eventFormatter;
    private final SyslogOutputStream outputStream;

    public SyslogAppender(
            final SyslogEventFormatter eventFormatter,
            final SyslogOutputStream outputStream) {

        this.eventFormatter = eventFormatter;
        this.outputStream = outputStream;
    }

    @Override
    protected void append(final ILoggingEvent eventObject) {
        if (!isStarted()) {
            return;
        }

        final String formattedEvent = eventFormatter.format(eventObject);

        try {
            outputStream.write(formattedEvent.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            addError("Failed to send message: " + formattedEvent, e);
        }
    }
}

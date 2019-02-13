package uk.gov.ida.dropwizard.logstash;

import ch.qos.logback.classic.net.SyslogAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.net.SyslogConstants;
import io.dropwizard.logging.SyslogAppenderFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

import java.text.MessageFormat;
import java.util.Locale;

public class SyslogEventFormatter {
    private final SyslogAppenderFactory.Facility facility;
    private final String hostname;
    private final String tag;
    private final Layout<ILoggingEvent> layout;

    public SyslogEventFormatter(
            final SyslogAppenderFactory.Facility facility,
            final String hostname,
            final String tag,
            final Layout<ILoggingEvent> layout) {

        this.facility = facility;
        this.hostname = hostname;
        this.tag = tag;
        this.layout = layout;
        this.layout.start();
    }

    public String format(final ILoggingEvent event) {
        final int priority = getPriority(event);
        final String timestamp = ISODateTimeFormat.dateTime().print(DateTime.now().withZone(DateTimeZone.UTC));
        final String encodedEvent = encodeEvent(event);
        return MessageFormat.format("<{0}>1 {1} {2} {3} - - - {4}", priority, timestamp, hostname, tag, encodedEvent);
    }

    private int getPriority(final ILoggingEvent event) {
        final String facilityString = facility.toString().toLowerCase(Locale.ENGLISH);
        final int facilityCode = SyslogAppender.facilityStringToint(facilityString);
        return facilityCode + getSeverity(event);
    }

    private int getSeverity(final ILoggingEvent event) {
        // Always return "notice" as this is what the rsyslog monitor we are moving from does
        return SyslogConstants.NOTICE_SEVERITY;
    }

    private String encodeEvent(final ILoggingEvent event) {
        return layout.doLayout(event);
    }
}

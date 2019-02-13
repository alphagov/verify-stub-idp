package uk.gov.ida.dropwizard.logstash;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.net.SyslogOutputStream;
import net.logstash.logback.layout.LogstashLayout;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import uk.gov.ida.dropwizard.logstash.support.UdpServer;

import static io.dropwizard.logging.SyslogAppenderFactory.Facility.LOCAL7;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public class LogstashSyslogAppenderIntegrationTest {

    private UdpServer udpServer = new UdpServer();

    @Before
    public void startUdpInterceptor() throws Exception {
        udpServer.start();
    }

    @After
    public void stopUdpInteceptor() throws Exception {
        udpServer.stop();
    }

    @After
    public void resetTime() throws Exception {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldSendLoggingEventToSyslogUdpSocket() throws Exception {
        DateTimeUtils.setCurrentMillisFixed(DateTime.parse("2014-04-01T00:00:00.000Z").getMillis());

        SyslogOutputStream syslogOutputStream = new SyslogOutputStream("localhost", udpServer.getLocalPort());
        SyslogEventFormatter eventFormatter = new SyslogEventFormatter(LOCAL7, "source-host", "test-event-tag", new LogstashLayout());
        SyslogAppender appender = new SyslogAppender(eventFormatter, syslogOutputStream);

        appender.start();
        appender.append(new LoggingEvent("my.logger", (Logger) LoggerFactory.getLogger("my.logger"), Level.INFO, "message", null, null));

        assertThat(udpServer.getReceivedPacket(), startsWith("<189>1 2014-04-01T00:00:00.000Z source-host test-event-tag - - - {"));
    }


}

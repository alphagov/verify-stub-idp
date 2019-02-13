package uk.gov.ida.dropwizard.logstash;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.net.SyslogOutputStream;
import ch.qos.logback.core.status.Status;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SyslogAppenderTest {

    private SyslogAppender appender;

    @Mock
    private SyslogEventFormatter syslogEventFormatter;
    @Mock
    private SyslogOutputStream outputStream;

    @Before
    public void setUp() throws Exception {
        appender = new SyslogAppender(syslogEventFormatter, outputStream);
        appender.start();
        when(syslogEventFormatter.format(any(ILoggingEvent.class))).thenReturn("");
    }

    @Test
    public void doAppend_shouldConvertEventToLogstashFormat() throws Exception {
        final String syslogMessage = UUID.randomUUID().toString();
        final ILoggingEvent event = mock(ILoggingEvent.class);
        when(syslogEventFormatter.format(event)).thenReturn(syslogMessage);

        appender.append(event);

        verify(outputStream).write(syslogMessage.getBytes());
    }

    @Test
    public void doAppend_shouldDoNothingIfAppenderHasNotBeenStarted() throws Exception {
        appender = new SyslogAppender(syslogEventFormatter, outputStream);

        appender.append(mock(ILoggingEvent.class));

        verify(outputStream, never()).write(Matchers.<byte[]>any());
    }

    @Test
    public void doAppend_shouldRecordAnErrorWhenWritingToSyslogFails() throws Exception {
        final IOException ioError = new IOException();
        doThrow(ioError).when(outputStream).write(Matchers.<byte[]>any());
        appender.setContext(new ContextBase());

        appender.append(mock(ILoggingEvent.class));

        final List<Status> statusList = appender.getStatusManager().getCopyOfStatusList();
        assertThat(statusList.size(), is(1));
        assertThat(statusList.get(0).getLevel(), is(Status.ERROR));
        assertThat(statusList.get(0).getThrowable(), Is.<Throwable>is(ioError));
    }
}

package uk.gov.ida.dropwizard.logstash;

import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.spi.ContextAware;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.JsonGenerator;
import io.dropwizard.logging.ConsoleAppenderFactory;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;
import net.logstash.logback.composite.AbstractJsonProvider;
import net.logstash.logback.composite.AbstractNestedJsonProvider;
import net.logstash.logback.composite.CompositeJsonFormatter;
import net.logstash.logback.composite.JsonProvider;
import net.logstash.logback.composite.JsonProviders;
import net.logstash.logback.composite.LogstashVersionJsonProvider;
import net.logstash.logback.composite.accessevent.AccessEventCompositeJsonFormatter;
import net.logstash.logback.composite.accessevent.AccessEventFormattedTimestampJsonProvider;
import net.logstash.logback.composite.accessevent.AccessMessageJsonProvider;
import net.logstash.logback.composite.accessevent.MethodJsonProvider;
import net.logstash.logback.composite.accessevent.RemoteUserJsonProvider;
import net.logstash.logback.composite.accessevent.StatusCodeJsonProvider;
import net.logstash.logback.encoder.AccessEventCompositeJsonEncoder;
import uk.gov.ida.dropwizard.logstash.typed.BytesField;
import uk.gov.ida.dropwizard.logstash.typed.MillisecondsField;

import java.io.IOException;

/**
 * Resets the field names to be modern (post-2013) logstash style
 * @see <a href="https://logstash.jira.com/browse/LOGSTASH-675">the logstash issue changing the name scheme</a>
 */
@JsonTypeName("access-logstash-console")
public class AccessLogstashConsoleAppenderFactory extends ConsoleAppenderFactory<IAccessEvent> {
    @Override
    public Appender<IAccessEvent> build(LoggerContext context,
                                         String applicationName,
                                         LayoutFactory<IAccessEvent> layout,
                                         LevelFilterFactory<IAccessEvent> levelFilterFactory,
                                         AsyncAppenderFactory<IAccessEvent> asyncAppenderFactory) {

        AccessEventCompositeJsonEncoder encoder = new AccessEventCompositeJsonEncoder() {
            @Override
            protected CompositeJsonFormatter<IAccessEvent> createFormatter() {
                return new CustomFormatter(this);
            }
        };
        encoder.setContext(context);
        encoder.start();

        final ConsoleAppender<IAccessEvent> appender = new ConsoleAppender<>();
        appender.setName("access-logstash-console-appender");
        appender.setContext(context);
        appender.setTarget(getTarget().get());
        appender.setEncoder(encoder);
        appender.start();

        return wrapAsync(appender, asyncAppenderFactory);
    }

    private class CustomFormatter extends AccessEventCompositeJsonFormatter {
        public CustomFormatter(ContextAware declaredOrigin) {
            super(declaredOrigin);

            JsonProviders<IAccessEvent> topLevel = getProviders();
            topLevel.addProvider(new AccessEventFormattedTimestampJsonProvider());
            topLevel.addProvider(new LogstashVersionJsonProvider<>());
            topLevel.addProvider(new AccessMessageJsonProvider() {{setFieldName("message");}});
            topLevel.addProvider(accessProvider());
        }

        private JsonProvider<IAccessEvent> accessProvider() {
            AbstractNestedJsonProvider<IAccessEvent> access = new AbstractNestedJsonProvider<IAccessEvent>() {};
            access.setFieldName("access");
            JsonProviders<IAccessEvent> accessProviders = access.getProviders();
            accessProviders.addProvider(new MethodJsonProvider() {{setFieldName("method");}});
            accessProviders.addProvider(new HttpVersionJsonProvider());
            accessProviders.addProvider(new RefererJsonProvider());
            accessProviders.addProvider(new UserAgentJsonProvider());
            accessProviders.addProvider(new HostJsonProvider());
            accessProviders.addProvider(new StatusCodeJsonProvider() {{setFieldName("response_code");}});
            accessProviders.addProvider(new UrlJsonProvider());
            accessProviders.addProvider(new RemoteIpJsonProvider());
            accessProviders.addProvider(new RemoteUserJsonProvider() {{setFieldName("user_name");}});
            accessProviders.addProvider(new BodySentJsonProvider());
            accessProviders.addProvider(new ElapsedTimeMsJsonProvider());
            return access;
        }
    }

    public static class ElapsedTimeMsJsonProvider extends AbstractJsonProvider<IAccessEvent> {
        @Override
        public void writeTo(JsonGenerator generator, IAccessEvent event) throws IOException {
            generator.writeObjectField("elapsed_time", new MillisecondsField(event.getElapsedTime()));
        }
    }

    public static class BodySentJsonProvider extends AbstractJsonProvider<IAccessEvent> {
        @Override
        public void writeTo(JsonGenerator generator, IAccessEvent event) throws IOException {
            generator.writeObjectField("body_sent", new BytesField(event.getContentLength()));
        }
    }

    /**
     * Provides http_version without leading "HTTP/", for consistency with filebeat apache2/nginx modules
     */
    public static class HttpVersionJsonProvider extends AbstractJsonProvider<IAccessEvent> {
        @Override
        public void writeTo(JsonGenerator generator, IAccessEvent event) throws IOException {
            generator.writeStringField("http_version", event.getProtocol().replace("HTTP/",""));
        }
    }

    public static class UrlJsonProvider extends AbstractJsonProvider<IAccessEvent> {
        @Override
        public void writeTo(JsonGenerator generator, IAccessEvent event) throws IOException {
            String firstLineWithoutMethod = firstLineOfRequest(event).replaceFirst("^[A-Z]* ", "");
            String requestedUrl = firstLineWithoutMethod.replaceFirst(" HTTP/.*$", "");
            generator.writeStringField("url", requestedUrl);
        }

        private String firstLineOfRequest(IAccessEvent event) {
            // This method is *terribly* named. It doesn't return a URL at all, but
            // rather the first line of the HTTP request.  For example, it might
            // return "GET /foo/bar?baz HTTP/1.1".
            // This method exists purely to give it a less confusing name
            return event.getRequestURL();
        }
    }

    public static class RemoteIpJsonProvider extends AbstractJsonProvider<IAccessEvent> {
        @Override
        public void writeTo(JsonGenerator generator, IAccessEvent event) throws IOException {
            generator.writeStringField("remote_ip", event.getRemoteAddr());
        }
    }

    public static class RefererJsonProvider extends AbstractJsonProvider<IAccessEvent> {
        @Override
        public void writeTo(JsonGenerator generator, IAccessEvent event) throws IOException {
            generator.writeStringField("referrer", event.getRequestHeader("Referer"));
        }
    }

    public static class UserAgentJsonProvider extends AbstractJsonProvider<IAccessEvent> {
        @Override
        public void writeTo(JsonGenerator generator, IAccessEvent event) throws IOException {
            generator.writeStringField("agent", event.getRequestHeader("User-Agent"));
        }
    }

    public static class HostJsonProvider extends AbstractJsonProvider<IAccessEvent> {
        @Override
        public void writeTo(JsonGenerator generator, IAccessEvent event) throws IOException {
            generator.writeStringField("host", event.getRequestHeader("Host"));
        }
    }
}

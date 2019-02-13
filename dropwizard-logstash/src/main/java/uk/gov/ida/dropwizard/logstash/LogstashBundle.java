package uk.gov.ida.dropwizard.logstash;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class LogstashBundle implements Bundle {
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        bootstrap.getObjectMapper().getSubtypeResolver().registerSubtypes(
                AccessLogstashConsoleAppenderFactory.class,
                LogstashConsoleAppenderFactory.class,
                LogstashSyslogAppenderFactory.class,
                LogstashFileAppenderFactory.class
        );
    }

    @Override
    public void run(Environment environment) {

    }
}

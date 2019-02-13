package uk.gov.ida.shared.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;

public class AllowUnknownPropertiesConfigurationFactoryFactory<T> extends DefaultConfigurationFactoryFactory<T> {
    @Override
    protected ObjectMapper configureObjectMapper(ObjectMapper objectMapper) {
        return objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
package uk.gov.ida.eventemitter;

import com.amazonaws.regions.Regions;

import java.net.URI;

public interface Configuration {

    boolean isEnabled();

    String getAccessKeyId();

    String getSecretAccessKey();

    Regions getRegion();

    URI getApiGatewayUrl();

    byte[] getEncryptionKey();
}

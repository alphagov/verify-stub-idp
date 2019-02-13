package uk.gov.ida.eventemitter.utils;

import com.amazonaws.regions.Regions;
import uk.gov.ida.eventemitter.Configuration;

import java.net.URI;

public final class TestConfiguration implements Configuration {

    private final boolean enabled;
    private final String accessKeyId;
    private final String accessSecretKey;
    private final Regions region;
    private final URI apiGatewayUrl;
    private final byte[] encryptionKey;

    public TestConfiguration(
            final boolean enabled,
            final String accessKeyId,
            final String accessSecretKey,
            final Regions region,
            final URI apiGatewayUrl,
            final byte[] encryptionKey) {

        this.enabled = enabled;
        this.accessKeyId = accessKeyId;
        this.accessSecretKey = accessSecretKey;
        this.region = region;
        this.apiGatewayUrl = apiGatewayUrl;
        this.encryptionKey = encryptionKey;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getAccessKeyId() {
        return accessKeyId;
    }

    @Override
    public String getSecretAccessKey() {
        return accessSecretKey;
    }

    @Override
    public Regions getRegion() {
        return region;
    }

    @Override
    public URI getApiGatewayUrl() {
        return apiGatewayUrl;
    }

    @Override
    public byte[] getEncryptionKey() {
        return encryptionKey;
    }

}

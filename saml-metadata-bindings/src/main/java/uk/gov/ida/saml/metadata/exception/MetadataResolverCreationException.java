package uk.gov.ida.saml.metadata.exception;

import java.net.URI;

import static java.lang.String.format;

public class MetadataResolverCreationException extends RuntimeException {
    public MetadataResolverCreationException(URI metadataUri, String message) {
        super(format("Failed to create metadata resolver for %s: %s", metadataUri, message));
    }
}

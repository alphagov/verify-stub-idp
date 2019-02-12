package uk.gov.ida.saml.metadata;

import org.opensaml.saml.metadata.resolver.impl.AbstractReloadingMetadataResolver;

public class StringBackedMetadataResolver extends AbstractReloadingMetadataResolver {
    private final String xml;

    public StringBackedMetadataResolver(String xml) {
        this.xml = xml;
    }

    @Override
    protected String getMetadataIdentifier() {
        return "metadata";
    }

    @Override
    protected byte[] fetchMetadata() {
        return xml.getBytes();
    }
}

package uk.gov.ida.saml.metadata;

import com.google.common.collect.ImmutableMultimap;
import io.dropwizard.servlets.tasks.Task;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.AbstractReloadingMetadataResolver;

import javax.inject.Inject;
import java.io.PrintWriter;

public class MetadataRefreshTask extends Task {
    private AbstractReloadingMetadataResolver metadataProvider;

    @Inject
    public MetadataRefreshTask(MetadataResolver metadataProvider) {
        super("metadata-refresh");
        this.metadataProvider = (AbstractReloadingMetadataResolver) metadataProvider;
    }

    @Override
    public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception {
        metadataProvider.refresh();
    }
}

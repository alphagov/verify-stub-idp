package uk.gov.ida.stub.idp.listeners;

import io.dropwizard.lifecycle.Managed;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.stub.idp.configuration.StubIdpConfiguration;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;

import javax.inject.Inject;
import java.io.File;

public class StubIdpsFileListener implements Managed {
    private static final Logger LOG = LoggerFactory.getLogger(StubIdpsFileListener.class);

    private final File stubIdpsYmlFileLocation;

    private final StubIdpConfiguration stubIdpConfiguration;
    private final IdpStubsRepository idpStubsRepository;
    private final FileAlterationMonitor fileAlterationMonitor;

    /**
     * @param stubIdpConfiguration which contains the stubIdpYmlFileLocation passed in as e.g. configuration/local/stub-idps.yml
     * @param idpStubsRepository which contains the idpStubRepository
     */
    @Inject
    public StubIdpsFileListener(StubIdpConfiguration stubIdpConfiguration, IdpStubsRepository idpStubsRepository) {
        this.stubIdpsYmlFileLocation = new File(stubIdpConfiguration.getStubIdpsYmlFileLocation());
        this.stubIdpConfiguration = stubIdpConfiguration;
        this.idpStubsRepository = idpStubsRepository;
        this.fileAlterationMonitor = new FileAlterationMonitor(stubIdpConfiguration.getStubIdpYmlFileRefresh().toMilliseconds());
    }

    @Override
    public void start() throws Exception {
        idpStubsRepository.load(stubIdpConfiguration.getStubIdpsYmlFileLocation());
        String fileName = stubIdpsYmlFileLocation.getName();
        String directory = stubIdpsYmlFileLocation.getParent();
        IOFileFilter fileFilter = FileFilterUtils.nameFileFilter(fileName);
        FileAlterationObserver fileAlterationObserver = new FileAlterationObserver(directory, fileFilter);
        FileAlterationListener listener = new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                LOG.info("Triggered file change on file: " + file.getAbsolutePath());
                idpStubsRepository.load(stubIdpConfiguration.getStubIdpsYmlFileLocation());
            }
        };
        fileAlterationObserver.addListener(listener);
        fileAlterationMonitor.addObserver(fileAlterationObserver);
        fileAlterationMonitor.start();
    }

    @Override
    public void stop() throws Exception {
        fileAlterationMonitor.stop();
    }
}

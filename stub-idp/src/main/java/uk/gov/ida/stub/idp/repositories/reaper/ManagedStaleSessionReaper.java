package uk.gov.ida.stub.idp.repositories.reaper;

import io.dropwizard.lifecycle.Managed;
import org.apache.log4j.Logger;
import uk.gov.ida.stub.idp.configuration.StubIdpConfiguration;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.SessionRepository;

import javax.inject.Inject;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Delete stale sessions
 */
public class ManagedStaleSessionReaper implements Managed {

    private static final Logger LOGGER = Logger.getLogger(ManagedStaleSessionReaper.class);

    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
    private final StaleSessionReaperConfiguration staleSessionReaperConfiguration;
    private final SessionRepository<IdpSession> verifySessionRepository;

    @Inject
    public ManagedStaleSessionReaper(StubIdpConfiguration stubIdpConfiguration,
                                     SessionRepository<IdpSession> verifySessionRepository) {
        this.staleSessionReaperConfiguration = stubIdpConfiguration.getStaleSessionReaperConfiguration();
        this.verifySessionRepository = verifySessionRepository;
    }

    @Override
    public void start() throws Exception {
        scheduledExecutorService.scheduleWithFixedDelay(new StaleSessionReaper(verifySessionRepository, staleSessionReaperConfiguration),
                staleSessionReaperConfiguration.getReaperFrequency().toStandardSeconds().getSeconds(),
                staleSessionReaperConfiguration.getReaperFrequency().toStandardSeconds().getSeconds(),
                TimeUnit.SECONDS);
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("shutting down; waiting for any active reapers to finish");
        scheduledExecutorService.shutdown();
        try {
            scheduledExecutorService.awaitTermination(60, TimeUnit.SECONDS);
        } catch(InterruptedException e) {
            LOGGER.warn("reaper was terminated before it had completed running");
        }
    }
}

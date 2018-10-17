package uk.gov.ida.stub.idp.repositories.reaper;

import org.apache.log4j.Logger;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.SessionRepository;

import static java.text.MessageFormat.format;

public class StaleSessionReaper implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(StaleSessionReaper.class);

    /**
     * this operates on all sessions in the database, both eidas and verify
     */
    private final SessionRepository<IdpSession> verifySessionRepository;
    private final StaleSessionReaperConfiguration staleSessionReaperConfiguration;

    public StaleSessionReaper(SessionRepository<IdpSession> verifySessionRepository,
                              StaleSessionReaperConfiguration staleSessionReaperConfiguration) {
        this.verifySessionRepository = verifySessionRepository;
        this.staleSessionReaperConfiguration = staleSessionReaperConfiguration;
    }

    @Override
    public void run() {
        final long sessionsInDatabaseBefore = verifySessionRepository.countSessionsInDatabase();
        LOGGER.info(format("{0} active sessions before reaping (eidas + verify)", sessionsInDatabaseBefore));
        final long staleSessionsToReap = verifySessionRepository.countSessionsOlderThan(staleSessionReaperConfiguration.getSessionIsStaleAfter());
        LOGGER.info(format("{0} session(s) (approx) are expected to be reaped (eidas + verify)", staleSessionsToReap));
        verifySessionRepository.deleteSessionsOlderThan(staleSessionReaperConfiguration.getSessionIsStaleAfter());
        final long sessionsInDatabaseAfter = verifySessionRepository.countSessionsInDatabase();
        LOGGER.info(format("{0} active sessions after reaping (eidas + verify)", sessionsInDatabaseAfter));
    }
}

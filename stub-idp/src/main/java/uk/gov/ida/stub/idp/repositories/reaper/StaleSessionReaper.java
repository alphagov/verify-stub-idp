package uk.gov.ida.stub.idp.repositories.reaper;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.SessionRepository;

import static java.text.MessageFormat.format;

public class StaleSessionReaper implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(StaleSessionReaper.class);

    /**
     * this operates on all sessions in the database, both eidas and verify
     */
    private final SessionRepository<IdpSession> verifySessionRepository;
    private final Duration sessionIsStaleAfter;

    public StaleSessionReaper(SessionRepository<IdpSession> verifySessionRepository,
                              StaleSessionReaperConfiguration staleSessionReaperConfiguration) {
        this.verifySessionRepository = verifySessionRepository;
        this.sessionIsStaleAfter = staleSessionReaperConfiguration.getSessionIsStaleAfter();
    }

    @Override
    public void run() {
        final long sessionsInDatabaseBefore = verifySessionRepository.countSessionsInDatabase();
        LOGGER.info(format("{0} active sessions before reaping (eidas + verify)", sessionsInDatabaseBefore));
        final long staleSessionsToReap = verifySessionRepository.countSessionsOlderThan(sessionIsStaleAfter);
        LOGGER.info(format("{0} session(s) (approx) are expected to be reaped (eidas + verify)", staleSessionsToReap));
        verifySessionRepository.deleteSessionsOlderThan(sessionIsStaleAfter);
        final long sessionsInDatabaseAfter = verifySessionRepository.countSessionsInDatabase();
        LOGGER.info(format("{0} active sessions after reaping (eidas + verify)", sessionsInDatabaseAfter));
    }
}

package uk.gov.ida.stub.idp.repositories;

import org.joda.time.Duration;
import uk.gov.ida.common.SessionId;

import java.util.Optional;

public interface SessionRepository<T extends Session> {
	boolean containsSession(SessionId sessionToken);
	Optional<T> get(SessionId sessionToken);
	SessionId updateSession(SessionId sessionToken, Session session);
	void deleteSession(SessionId sessionToken);
	Optional<T> deleteAndGet(SessionId sessionToken);
	long countSessionsOlderThan(Duration duration);
	void deleteSessionsOlderThan(Duration duration);
	long countSessionsInDatabase();
}

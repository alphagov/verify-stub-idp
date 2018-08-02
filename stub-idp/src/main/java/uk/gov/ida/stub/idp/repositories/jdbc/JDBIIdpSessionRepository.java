package uk.gov.ida.stub.idp.repositories.jdbc;

import org.jdbi.v3.core.Jdbi;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.IdpSessionRepository;
import uk.gov.ida.stub.idp.repositories.SessionRepositoryBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JDBIIdpSessionRepository extends SessionRepositoryBase<IdpSession> implements IdpSessionRepository {
	@Inject
	public JDBIIdpSessionRepository(Jdbi jdbi) {
		super(IdpSession.class, jdbi);
	}

	public SessionId createSession(IdpSession session) {
		return insertSession(session.getSessionId(), session);
	}
}

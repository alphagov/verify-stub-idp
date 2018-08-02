package uk.gov.ida.stub.idp.repositories.jdbc;

import org.jdbi.v3.core.Jdbi;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.repositories.EidasSession;
import uk.gov.ida.stub.idp.repositories.EidasSessionRepository;
import uk.gov.ida.stub.idp.repositories.SessionRepositoryBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JDBIEidasSessionRepository extends SessionRepositoryBase<EidasSession> implements EidasSessionRepository {
	@Inject
	public JDBIEidasSessionRepository(Jdbi jdbi) {
		super(EidasSession.class, jdbi);
	}

	public SessionId createSession(EidasSession session) {
		return insertSession(session.getSessionId(), session);
	}
}

package uk.gov.ida.stub.idp.repositories;

import uk.gov.ida.common.SessionId;

public interface IdpSessionRepository extends SessionRepository<IdpSession> {
	SessionId createSession(IdpSession session);
}

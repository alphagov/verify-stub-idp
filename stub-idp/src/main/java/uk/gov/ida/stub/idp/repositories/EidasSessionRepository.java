package uk.gov.ida.stub.idp.repositories;

import uk.gov.ida.common.SessionId;

public interface EidasSessionRepository extends SessionRepository<EidasSession> {
	SessionId createSession(EidasSession session);
}

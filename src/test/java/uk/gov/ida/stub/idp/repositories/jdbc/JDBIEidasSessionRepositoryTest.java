package uk.gov.ida.stub.idp.repositories.jdbc;

import org.jdbi.v3.core.Jdbi;
import org.junit.Before;
import org.junit.Test;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.domain.EidasAuthnRequest;
import uk.gov.ida.stub.idp.domain.EidasUser;
import uk.gov.ida.stub.idp.repositories.EidasSession;
import uk.gov.ida.stub.idp.repositories.jdbc.migrations.DatabaseMigrationRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class JDBIEidasSessionRepositoryTest {
	private Jdbi jdbi;
	private JDBIEidasSessionRepository repository;
	private final String DATABASE_URL = "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";

	@Before
	public void setUp() {
		new DatabaseMigrationRunner().runMigration(DATABASE_URL);

		jdbi = Jdbi.create(DATABASE_URL);
		repository = new JDBIEidasSessionRepository(jdbi);
	}
	
	@Test
	public void createSession_shouldCreateEidasSessionAndStoreInDatabase() {
		EidasAuthnRequest authnRequest = new EidasAuthnRequest("7cb0ba32-4ebd-4291-8901-c647d4687572", "test-issuer", "", "", Arrays.asList());

		SessionId eidasSessionId = SessionId.createNewSessionId();
		EidasSession session = new EidasSession(eidasSessionId, authnRequest, "test-relay-state", Collections.emptyList(), Collections.emptyList(), Optional.empty(), Optional.empty());
		repository.createSession(session);
		
		String expectedSerializedSession = "{{\"sessionId\":\""+ eidasSessionId +"\",\"eidasAuthnRequest\":{\"requestId\":\"7cb0ba32-4ebd-4291-8901-c647d4687572\",\"issuer\":\"test-issuer\",\"destination\":\"\",\"requestedLoa\":\"\",\"attributes\":[]},\"relayState\":\"test-relay-state\",\"validHints\":[],\"invalidHints\":[],\"languageHint\":{\"value\":null,\"present\":false},\"registration\":{\"value\":null,\"present\":false},\"eidasUser\":{\"value\":null,\"present\":false}}}";

		jdbi.useHandle(handle -> {
			Optional<String> result = handle.select("select session_data from stub_idp_session where session_id = ?", eidasSessionId.toString())
					.mapTo(String.class)
					.findFirst();

			assertThat(result.isPresent()).isEqualTo(true);
			assertThat(result.get()).isEqualTo(expectedSerializedSession);
		});
	}

	@Test
	public void get_shouldReturnPopulatedIdpSession_whenSessionExists() {
		EidasAuthnRequest authnRequest = new EidasAuthnRequest("7cb0ba32-4ebd-4291-8901-c647d4687572", "test-issuer", "", "", Arrays.asList());

		EidasSession expectedSession = new EidasSession(SessionId.createNewSessionId(), authnRequest, "test-relay-state", Collections.emptyList(), Collections.emptyList(), Optional.empty(), Optional.empty());
		expectedSession.setEidasUser(new EidasUser(null, null, null, null, null, null));
		SessionId insertedSessionId = repository.createSession(expectedSession);

		Optional<EidasSession> actualSession = repository.get(insertedSessionId);

		assertThat(actualSession.isPresent()).isEqualTo(true);
		assertThat(actualSession.get()).isInstanceOf(EidasSession.class);
		assertThat(actualSession.get()).isEqualToComparingFieldByFieldRecursively(expectedSession);
	}
}

package uk.gov.ida.stub.idp.repositories.jdbc;

import org.jdbi.v3.core.Jdbi;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.hub.domain.IdaAuthnRequestFromHub;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.jdbc.migrations.DatabaseMigrationRunner;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class JDBIIdpSessionRepositoryTest {
	private Jdbi jdbi;
	private JDBIIdpSessionRepository repository;
	private final String DATABASE_URL = "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";
	
	@Before
	public void setUp() {
		new DatabaseMigrationRunner().runMigration(DATABASE_URL);

		jdbi = Jdbi.create(DATABASE_URL);
		repository = new JDBIIdpSessionRepository(jdbi);
	}
	
	@Test
	public void createSession_shouldCreateIdpSessionAndStoreInDatabase() {
		DateTime authnRequestIssueTime = DateTime.parse("25-04-2018 11:24:00", DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss"));
		IdaAuthnRequestFromHub authnRequest = new IdaAuthnRequestFromHub("155a37d3-5a9d-4cd0-b68a-158717b85202", "test-issuer", authnRequestIssueTime, Arrays.asList(), Optional.empty(), null, null, AuthnContextComparisonTypeEnumeration.EXACT);
		IdpSession session = new IdpSession(SessionId.createNewSessionId(), authnRequest, "test-relay-state", Arrays.asList(), Arrays.asList(), Optional.empty(), Optional.empty());
		SessionId insertedSessionId = repository.createSession(session);
		String expectedSerializedSession = "{{\"sessionId\":\""+ session.getSessionId().toString() +"\",\"idaAuthnRequestFromHub\":{\"id\":\"155a37d3-5a9d-4cd0-b68a-158717b85202\",\"issuer\":\"test-issuer\",\"issueInstant\":"+ authnRequestIssueTime.getMillis() +",\"levelsOfAssurance\":[],\"forceAuthentication\":{\"value\":null,\"present\":false},\"sessionExpiryTimestamp\":null,\"comparisonType\":{\"comparisonType\":\"exact\"},\"destination\":null},\"relayState\":\"test-relay-state\",\"validHints\":[],\"invalidHints\":[],\"languageHint\":{\"value\":null,\"present\":false},\"registration\":{\"value\":null,\"present\":false},\"idpUser\":{\"value\":null,\"present\":false}}}";
		
		jdbi.useHandle(handle -> {
			Optional<String> result = handle.select("select session_data from stub_idp_session where session_id = ?", insertedSessionId.toString())
				.mapTo(String.class)
				.findFirst();
				
			assertThat(result.isPresent()).isEqualTo(true);
			assertThat(result.get()).isEqualTo(expectedSerializedSession);
		});
	}
	
	@Test
	public void get_shouldReturnEmptyOptional_whenSessionDoesNotExist() {
		SessionId nonExistentSessionId = SessionId.createNewSessionId();
		
		Optional<IdpSession> result = repository.get(nonExistentSessionId);
		
		assertThat(result.isPresent()).isEqualTo(false);
	}
	
	@Test
	public void get_shouldReturnPopulatedIdpSession_whenSessionExists() {
		DateTime authnRequestIssueTime = new DateTime(2018, 4, 25, 11, 24, 0, DateTimeZone.UTC);
		IdaAuthnRequestFromHub authnRequest = new IdaAuthnRequestFromHub("155a37d3-5a9d-4cd0-b68a-158717b85202", "test-issuer", authnRequestIssueTime, Arrays.asList(), Optional.empty(), null, null, AuthnContextComparisonTypeEnumeration.EXACT);
		IdpSession session = new IdpSession(SessionId.createNewSessionId(), authnRequest, "test-relay-state", Arrays.asList(), Arrays.asList(), Optional.empty(), Optional.empty());
		SessionId insertedSessionId = repository.createSession(session);
		IdpSession expectedSession = new IdpSession(insertedSessionId, authnRequest, "test-relay-state", Arrays.asList(), Arrays.asList(), Optional.empty(), Optional.empty());
		
		Optional<IdpSession> actualSession = repository.get(insertedSessionId);
		
		assertThat(actualSession.isPresent()).isEqualTo(true);
		assertThat(actualSession.get()).isInstanceOf(IdpSession.class);
		assertThat(actualSession.get()).isEqualToComparingFieldByFieldRecursively(expectedSession);
	}
	
	@Test
	public void updateSession_shouldNotThrowException_whenSessionDoesNotExist() {
		DateTime authnRequestIssueTime = DateTime.parse("25-04-2018 11:24:00", DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss"));
		IdaAuthnRequestFromHub authnRequest = new IdaAuthnRequestFromHub("155a37d3-5a9d-4cd0-b68a-158717b85202", "test-issuer", authnRequestIssueTime, Arrays.asList(), Optional.empty(), null, null, AuthnContextComparisonTypeEnumeration.EXACT);

		IdpSession session = new IdpSession(SessionId.createNewSessionId(), authnRequest, "test-relay-state", Arrays.asList(), Arrays.asList(), Optional.empty(), Optional.empty());
		SessionId insertedSessionId = repository.createSession(session);
		session = repository.get(insertedSessionId).get();
		session.getIdaAuthnRequestFromHub().getLevelsOfAssurance().add(AuthnContext.LEVEL_4);
		
		repository.updateSession(SessionId.createNewSessionId(), session);
	}
	
	@Test
	public void updateSession_shouldUpdateStoredSessionInDatabase_whenSessionExists() {
		DateTime authnRequestIssueTime = DateTime.parse("25-04-2018 11:24:00", DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss"));
		IdaAuthnRequestFromHub authnRequest = new IdaAuthnRequestFromHub("155a37d3-5a9d-4cd0-b68a-158717b85202", "test-issuer", authnRequestIssueTime, Arrays.asList(), Optional.empty(), null, null, AuthnContextComparisonTypeEnumeration.EXACT);

		IdpSession session = new IdpSession(SessionId.createNewSessionId(), authnRequest, "test-relay-state", Arrays.asList(), Arrays.asList(), Optional.empty(), Optional.empty());
		SessionId insertedSessionId = repository.createSession(session);
		IdpSession expectedSession = repository.get(insertedSessionId).get();
		expectedSession.getIdaAuthnRequestFromHub().getLevelsOfAssurance().add(AuthnContext.LEVEL_4);
		repository.updateSession(insertedSessionId, expectedSession);
		IdpSession actualSession = repository.get(insertedSessionId).get();
		
		assertThat(actualSession).isEqualToComparingFieldByFieldRecursively(expectedSession);
	}

	@Test
	public void deleteSession_shouldDeleteSessionFromDatabase_whenSessionExists() {
		DateTime authnRequestIssueTime = new DateTime(2018, 4, 25, 11, 24, 0, DateTimeZone.UTC);
		IdaAuthnRequestFromHub authnRequest = new IdaAuthnRequestFromHub("155a37d3-5a9d-4cd0-b68a-158717b85202", "test-issuer", authnRequestIssueTime, Arrays.asList(), Optional.empty(), null, null, AuthnContextComparisonTypeEnumeration.EXACT);

		IdpSession session = new IdpSession(SessionId.createNewSessionId(), authnRequest, "test-relay-state", Arrays.asList(), Arrays.asList(), Optional.empty(), Optional.empty());
		SessionId insertedSessionId = repository.createSession(session);
		
		repository.deleteSession(insertedSessionId);
		
		assertThat(repository.containsSession(insertedSessionId)).isEqualTo(false);
	}
}

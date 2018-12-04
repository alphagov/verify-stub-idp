package uk.gov.ida.stub.idp.repositories;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.hub.domain.IdaAuthnRequestFromHub;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.domain.IdpHint;
import uk.gov.ida.stub.idp.domain.IdpLanguageHint;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class IdpSession extends Session {
	private final IdaAuthnRequestFromHub idaAuthnRequestFromHub;
	private Optional<DatabaseIdpUser> idpUser = Optional.empty();
	private final Optional<UUID> singleIdpJourneyId;

	@JsonCreator
	public IdpSession(@JsonProperty("sessionId") SessionId sessionId, @JsonProperty("idaAuthnRequestFromHub") IdaAuthnRequestFromHub idaAuthnRequestFromHub, @JsonProperty("relayState") String relayState,
					  @JsonProperty("validHints") List<IdpHint> validHints, @JsonProperty("invalidHints") List<String> invalidHints, @JsonProperty("languageHint") Optional<IdpLanguageHint> languageHint,
					  @JsonProperty("registration") Optional<Boolean> registration, @JsonProperty("singleIdpJourneyId") Optional<UUID> singleIdpJourneyId) {
		super(sessionId, relayState, validHints, invalidHints, languageHint, registration);
		this.idaAuthnRequestFromHub = idaAuthnRequestFromHub;
		this.singleIdpJourneyId = singleIdpJourneyId;
	}

	public IdpSession(SessionId sessionId) {
		this(sessionId, null, null, null, null, null, null, null);
	}

	public IdaAuthnRequestFromHub getIdaAuthnRequestFromHub() {
		return idaAuthnRequestFromHub;
	}

	public Optional<DatabaseIdpUser> getIdpUser() {
		return idpUser;
	}
	
	public void setIdpUser(Optional<DatabaseIdpUser> idpUser) {
		this.idpUser = idpUser;
	}

	public Optional<UUID> getSingleIdpJourneyId() {
		return singleIdpJourneyId;
	}
}

package uk.gov.ida.stub.idp.repositories;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.domain.EidasAuthnRequest;
import uk.gov.ida.stub.idp.domain.EidasUser;
import uk.gov.ida.stub.idp.domain.IdpHint;
import uk.gov.ida.stub.idp.domain.IdpLanguageHint;

import java.util.List;
import java.util.Optional;

public class EidasSession extends Session {
	private final EidasAuthnRequest eidasAuthnRequest;
	private Optional<EidasUser> eidasUser = Optional.empty();

	@JsonCreator
	public EidasSession(@JsonProperty("sessionId") SessionId sessionId,
						@JsonProperty("eidasAuthnRequest") EidasAuthnRequest eidasAuthnRequest,
						@JsonProperty("relayState") String relayState,
						@JsonProperty("validHints") List<IdpHint> validHints,
						@JsonProperty("invalidHints") List<String> invalidHints,
						@JsonProperty("languageHint") Optional<IdpLanguageHint> languageHint,
						@JsonProperty("registration") Optional<Boolean> registration,
						@JsonProperty("csrfToken") String csrfToken) {
		super(sessionId, relayState, validHints, invalidHints, languageHint, registration, csrfToken);
		this.eidasAuthnRequest = eidasAuthnRequest;
	}

	public EidasSession(SessionId sessionId,
						EidasAuthnRequest eidasAuthnRequest,
						String relayState,
						List<IdpHint> validHints,
						List<String> invalidHints,
						Optional<IdpLanguageHint> languageHint,
						Optional<Boolean> registration) {
		super(sessionId, relayState, validHints, invalidHints, languageHint, registration, null);
		this.eidasAuthnRequest = eidasAuthnRequest;
	}

	public EidasAuthnRequest getEidasAuthnRequest() {
		return eidasAuthnRequest;
	}

	public Optional<EidasUser> getEidasUser() {
		return eidasUser;
	}

	public void setEidasUser(EidasUser eidasUser) {
		this.eidasUser = Optional.ofNullable(eidasUser);
	}
}

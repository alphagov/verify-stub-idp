package uk.gov.ida.stub.idp.repositories;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.jdbi.v3.core.Jdbi;
import org.joda.time.Duration;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.saml.hub.domain.IdaAuthnRequestFromHub;
import uk.gov.ida.stub.idp.exceptions.SessionSerializationException;
import uk.gov.ida.stub.idp.repositories.jdbc.mixins.AuthnContextComparisonTypeMixin;
import uk.gov.ida.stub.idp.repositories.jdbc.mixins.GenderMixin;
import uk.gov.ida.stub.idp.repositories.jdbc.mixins.IdaAuthnRequestFromHubMixin;
import uk.gov.ida.stub.idp.repositories.jdbc.mixins.XmlObjectMixin;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeJson;

public abstract class SessionRepositoryBase<T extends Session> implements SessionRepository<T> {
    private final Jdbi jdbi;
    private final ObjectMapper objectMapper;
    private final Class<T> sessionType;

    public SessionRepositoryBase(Class<T> sessionType, Jdbi jdbi) {
        this.sessionType = sessionType;
        this.jdbi = jdbi;
        this.objectMapper = createObjectMapperForSerialization();
    }
    
    public boolean containsSession(SessionId sessionToken) {
        return jdbi.withHandle(handle -> handle.select(
                "select count(1) from stub_idp_session where session_id = ?", sessionToken.toString())
                .mapTo(Boolean.class)
                .findOnly());
    }
    
    public Optional<T> get(SessionId sessionToken) {
        Optional<String> sessionData = jdbi.withHandle(handle -> handle.select(
                "select session_data from stub_idp_session where session_id = ?", sessionToken.toString())
                .mapTo(String.class)
                .findFirst());

        if (!sessionData.isPresent()) {
            return Optional.empty();
        }

        try {
            String serializedSession = sessionData.get();
            T session = objectMapper.readValue(unescapeJson(serializedSession.substring(1, serializedSession.length() - 1)), sessionType);

            return Optional.of(session);
        } catch (JsonParseException e) {
            return cleanupSession(sessionToken);
        } catch (JsonMappingException e) {
            return cleanupSession(sessionToken);
        } catch (IOException e) {
            return cleanupSession(sessionToken);
        }
    }
    
    public SessionId updateSession(SessionId sessionToken, Session session) {
        try {
            String serializedSession = objectMapper.writeValueAsString(session);

            jdbi.withHandle(handle ->
                    handle.createUpdate(
                            "UPDATE stub_idp_session SET session_data = to_json(:sessionData), last_modified = :lastModified WHERE session_id = :sessionId")
                            .bind("sessionId", sessionToken.toString())
                            .bind("sessionData", serializedSession)
                            .bind("lastModified", Instant.now())
                            .execute());
        } catch (JsonProcessingException e) {
            throw new SessionSerializationException("Unable to create session update.");
        }

        return sessionToken;
    }
    
    
    public void deleteSession(SessionId sessionToken) {
        jdbi.withHandle(handle -> handle.execute("DELETE FROM stub_idp_session WHERE session_id = ?", sessionToken.toString()));
    }

    public Optional<T> deleteAndGet(SessionId sessionToken) {
        Optional<T> session = get(sessionToken);
        deleteSession(sessionToken);

        return session;
    }

    public long countSessionsOlderThan(Duration duration) {
        return jdbi.withHandle(handle -> handle.select("select count(*) from stub_idp_session where last_modified < :lastModified")
                .bind("lastModified", Instant.now().minusSeconds(duration.getStandardSeconds()))
                .mapTo(Long.class)
                .findOnly());
    }

    public void deleteSessionsOlderThan(Duration duration) {
        jdbi.withHandle(handle -> handle.execute("DELETE FROM stub_idp_session where last_modified < ?", Instant.now().minusSeconds(duration.getStandardSeconds())));
    }

    public long countSessionsInDatabase() {
        return jdbi.withHandle(handle -> handle.select(
                "select count(*) from stub_idp_session")
                .mapTo(Long.class)
                .findOnly());
    }
    
    protected SessionId insertSession(SessionId sessionToken, T session) {
        try {
            String serializedSession = objectMapper.writeValueAsString(session);

            jdbi.withHandle(handle ->
                handle.createUpdate(
                        "INSERT INTO stub_idp_session(session_id, session_data) VALUES (:sessionId, to_json(:sessionData))")
                        .bind("sessionId", sessionToken.toString())
                        .bind("sessionData", serializedSession)
                        .execute());
        } catch (JsonProcessingException e) {
            throw new SessionSerializationException("Unable to create session update.");
        }

        return sessionToken;
    }

    private Optional cleanupSession(SessionId sessionToken) {
        deleteSession(sessionToken);

        return Optional.empty();
    }

    private ObjectMapper createObjectMapperForSerialization() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.addMixIn(XMLObject.class, XmlObjectMixin.class);
        objectMapper.addMixIn(IdaAuthnRequestFromHub.class, IdaAuthnRequestFromHubMixin.class);
        objectMapper.addMixIn(AuthnContextComparisonTypeEnumeration.class, AuthnContextComparisonTypeMixin.class);
        objectMapper.addMixIn(Gender.class, GenderMixin.class);
        objectMapper.registerModule(new JodaModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }
}

package uk.gov.ida.stub.idp.services;

import org.apache.commons.lang.StringEscapeUtils;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.hub.domain.IdaAuthnRequestFromHub;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.domain.EidasAuthnRequest;
import uk.gov.ida.stub.idp.domain.IdpHint;
import uk.gov.ida.stub.idp.domain.IdpLanguageHint;
import uk.gov.ida.stub.idp.repositories.EidasSession;
import uk.gov.ida.stub.idp.repositories.EidasSessionRepository;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.IdpSessionRepository;

import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class AuthnRequestReceiverService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthnRequestReceiverService.class);

    private final Function<String, IdaAuthnRequestFromHub> samlRequestTransformer;
    private final IdpSessionRepository idpSessionRepository;
    private final EidasSessionRepository eidasSessionRepository;
    private final Function<String, AuthnRequest> stringAuthnRequestTransformer;

    public static class SessionCreated {
        private URI nextLocation;
        private SessionId idpSessionId;

        public SessionCreated(URI nextLocation, SessionId idpSessionId) {
            this.nextLocation = nextLocation;
            this.idpSessionId = idpSessionId;
        }

        public SessionId getIdpSessionId() {
            return idpSessionId;
        }

        public URI getNextLocation() {
            return nextLocation;
        }
    }

    @Inject
    public AuthnRequestReceiverService(
            Function<String, IdaAuthnRequestFromHub> samlRequestTransformer,
            IdpSessionRepository idpSessionRepository,
            EidasSessionRepository eidasSessionRepository,
            Function<String, AuthnRequest> stringToAuthnRequestTransformer) {

        this.samlRequestTransformer = samlRequestTransformer;
        this.idpSessionRepository = idpSessionRepository;
        this.eidasSessionRepository = eidasSessionRepository;
        this.stringAuthnRequestTransformer = stringToAuthnRequestTransformer;
    }

    public SessionCreated handleAuthnRequest(String idpName, String samlRequest, Set<String> idpHints, Optional<Boolean> registration, String relayState, Optional<IdpLanguageHint> languageHint) {
        final List<IdpHint> validHints = new ArrayList<>();
        final List<String> invalidHints = new ArrayList<>();
        validateHints(idpHints, validHints, invalidHints);

        final IdaAuthnRequestFromHub idaRequestFromHub = samlRequestTransformer.apply(samlRequest);
        IdpSession session = new IdpSession(SessionId.createNewSessionId(), idaRequestFromHub, relayState, validHints, invalidHints, languageHint, registration);
        final SessionId idpSessionId = idpSessionRepository.createSession(session);

        UriBuilder uriBuilder;
        if (registration.isPresent() && registration.get()) {
            uriBuilder = UriBuilder.fromPath(Urls.REGISTER_RESOURCE);
        } else {
            uriBuilder = UriBuilder.fromPath(Urls.LOGIN_RESOURCE);
        }

        return new SessionCreated(uriBuilder.build(idpName), idpSessionId);
    }

    public SessionCreated handleEidasAuthnRequest(String schemeId, String samlRequest, String relayState, Optional<IdpLanguageHint> languageHint) {
        AuthnRequest authnRequest = stringAuthnRequestTransformer.apply(samlRequest);
        EidasAuthnRequest eidasAuthnRequest = EidasAuthnRequest.buildFromAuthnRequest(authnRequest);
        EidasSession session = new EidasSession(SessionId.createNewSessionId(), eidasAuthnRequest, relayState, Collections.emptyList(), Collections.emptyList(), languageHint, Optional.empty());
        final SessionId idpSessionId = eidasSessionRepository.createSession(session);

        UriBuilder uriBuilder = UriBuilder.fromPath(Urls.EIDAS_LOGIN_RESOURCE);

        return new SessionCreated(uriBuilder.build(schemeId), idpSessionId);
    }

    private void validateHints(Set<String> idpHints, List<IdpHint> validHints, List<String> invalidHints) {
        if (idpHints != null && !idpHints.isEmpty()) {
            for (String hint : idpHints) {
                try {
                    validHints.add(IdpHint.valueOf(hint));
                } catch (IllegalArgumentException e) {
                    // this is a hint that stub-idp does not know about, and it should be able
                    // to deal with such hints.  Also sanitize string
                    invalidHints.add(StringEscapeUtils.escapeHtml(hint));
                }
            }
            if (!validHints.isEmpty()) {
                LOG.info("Received known hints: {}", validHints);
            }
            if (!invalidHints.isEmpty()) {
                LOG.info("Received unknown hints: {}", invalidHints);
            }
        }
    }
}

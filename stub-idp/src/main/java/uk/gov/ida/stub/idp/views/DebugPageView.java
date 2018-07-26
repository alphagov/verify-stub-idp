package uk.gov.ida.stub.idp.views;

import uk.gov.ida.stub.idp.domain.IdpHint;
import uk.gov.ida.stub.idp.domain.IdpLanguageHint;
import uk.gov.ida.stub.idp.repositories.IdpSession;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DebugPageView extends IdpPageView {

    private final IdpSession session;

    public DebugPageView(String name, String idpId, String assetId, IdpSession session) {
        super("debugPage.ftl", name, idpId, null, assetId);
        this.session = session;
    }

    public String getPageTitle() {
        return String.format("System information for %s", getName());
    }

    public List<IdpHint> getKnownHints() {
        return session.getValidHints();
    }

    public List<String> getUnknownHints() {
        return session.getInvalidHints();
    }

    public IdpLanguageHint getLanguageHint() {
        return session.getLanguageHint().orElse(null);
    }

    public Optional<Boolean> getRegistration() {
        return session.isRegistration();
    }

    public String getComparisonType() {
        return session.getIdaAuthnRequestFromHub().getComparisonType().toString();
    }

    public List<String> getAuthnContexts() {
        return session.getIdaAuthnRequestFromHub().getLevelsOfAssurance().stream()
            .map(Enum::name).collect(Collectors.toList());
    }

    public String getRelayState() {
        return session.getRelayState();
    }

    public String getSessionId() {
        return session.getSessionId().getSessionId();
    }

    public String getSamlRequestId() {
        return session.getIdaAuthnRequestFromHub().getId();
    }

    public String getAuthnRequestIssuer() {
        return session.getIdaAuthnRequestFromHub().getIssuer();
    }
}

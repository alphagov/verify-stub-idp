package uk.gov.ida.stub.idp.views;

import uk.gov.ida.stub.idp.domain.IdpLanguageHint;
import uk.gov.ida.stub.idp.repositories.EidasSession;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

public class EidasDebugPageView extends IdpPageView {

    private final EidasSession session;

    public EidasDebugPageView(String displayName, String friendlyId, String assetId, EidasSession session) {
        super("eidasDebugPage.ftl", displayName, friendlyId, null, assetId, Optional.empty());
        this.session = session;
    }

    public String getPageTitle() {
        return String.format("System information for %s", getName());
    }

    public IdpLanguageHint getLanguageHint() {
        return session.getLanguageHint().orElse(null);
    }

    public String getRelayState() {
        return session.getRelayState();
    }

    public String getSessionId() {
        return session.getSessionId().getSessionId();
    }

    public List<String> getRequestedAttributes() {
        return session.getEidasAuthnRequest().getAttributes().stream().map(a -> format("attribute: {0}, required: {1}", a.getName(), a.isRequired())).collect(Collectors.toList());
    }

    public String getRequestedLevelOfAssurance() {
        return session.getEidasAuthnRequest().getRequestedLoa();
    }

    public String getSamlRequestId() {
        return session.getEidasAuthnRequest().getRequestId();
    }

    public String getAuthnRequestIssuer() {
        return session.getEidasAuthnRequest().getIssuer();
    }
}

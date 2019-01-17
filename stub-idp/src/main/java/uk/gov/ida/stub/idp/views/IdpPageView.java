package uk.gov.ida.stub.idp.views;

import io.dropwizard.views.View;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class IdpPageView extends View {
    private final String subPageTemplateName;
    private final String name;
    private final String idpId;
    private final String errorMessage;
    private final String assetId;
    private final Optional<String> csrfToken;

    public IdpPageView(String subPageTemplateName, String name, String idpId, String errorMessage, String assetId, Optional<String> csrfToken) {
        super("idpPage.ftl", StandardCharsets.UTF_8);

        this.subPageTemplateName = subPageTemplateName;
        this.name = name;
        this.idpId = idpId;
        this.errorMessage = errorMessage;
        this.assetId = assetId;
        this.csrfToken = csrfToken;
    }

    public String getPageTitle() {
        return "No page title set.";
    }

    public String getAssetId() {
        return assetId;
    }

    public String getIdpId() {
        return idpId;
    }

    public String getName() {
        return name;
    }

    public String getErrorMessage() {
        if (errorMessage == null) {
            return "";
        }
        return errorMessage;
    }

    public String getSubPageTemplateName() {
        return subPageTemplateName;
    }

    public Optional<String> getCsrfToken() {
        return csrfToken;
    }
}

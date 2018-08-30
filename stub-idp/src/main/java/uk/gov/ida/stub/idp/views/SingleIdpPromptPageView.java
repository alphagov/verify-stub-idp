package uk.gov.ida.stub.idp.views;

import uk.gov.ida.stub.idp.domain.Service;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class SingleIdpPromptPageView extends IdpPageView {

    private final List<Service> services;
    private final URI verifySubmissionUrl;
    private final UUID uniqueId;

    public SingleIdpPromptPageView(String name, String idpId, String errorMessage, String assetId, List<Service> services, URI verifySubmissionUrl, UUID uniqueId) {
        super("singleIdpPromptPage.ftl", name, idpId, errorMessage, assetId);
        services.sort(Comparator.comparing(Service::getServiceCategory));
        this.services = services;
        this.uniqueId = uniqueId;
        this.verifySubmissionUrl = verifySubmissionUrl;
    }

    public String getPageTitle() {
        return String.format("Welcome to %s", getName());
    }

    public List<Service> getServices() {
        return services;
    }

    public URI getVerifySubmissionUrl() {
        return verifySubmissionUrl;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }
}

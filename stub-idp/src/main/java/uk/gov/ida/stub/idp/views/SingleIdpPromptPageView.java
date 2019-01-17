package uk.gov.ida.stub.idp.views;

import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.domain.Service;
import uk.gov.ida.stub.idp.views.helpers.IdpUserHelper;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SingleIdpPromptPageView extends IdpPageView {

    private final List<Service> services;
    private final URI verifySubmissionUrl;
    private final UUID uniqueId;
    private final IdpUserHelper idpUserHelper;

    public SingleIdpPromptPageView(String name, String idpId, String errorMessage, String assetId, List<Service> services, URI verifySubmissionUrl, UUID uniqueId) {
        this(name, idpId, errorMessage, assetId, services, verifySubmissionUrl, uniqueId,null);
    }

    public SingleIdpPromptPageView(String name, String idpId, String errorMessage, String assetId, List<Service> services, URI verifySubmissionUrl, UUID uniqueId, DatabaseIdpUser idpUser) {
        super("singleIdpPromptPage.ftl", name, idpId, errorMessage, assetId, Optional.empty());
        services.sort(Comparator.comparing(Service::getServiceCategory));
        this.services = services;
        this.uniqueId = uniqueId;
        this.verifySubmissionUrl = verifySubmissionUrl;
        this.idpUserHelper = new IdpUserHelper(idpUser);
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

    public DatabaseIdpUser getIdpUser() {
        return idpUserHelper.getIdpUser();
    }

    public String getFirstName() {
        return idpUserHelper.getFirstName();
    }

    public String getSurname() {
        return idpUserHelper.getSurname();
    }

    public String getDateOfBirth() {
        return idpUserHelper.getDateOfBirth();
    }

    public String getGender() {
        return idpUserHelper.getGender();
    }

    public Address getAddress() {
        return idpUserHelper.getAddress();
    }

    public String getLoa() {
        return idpUserHelper.getLoa();
    }
}

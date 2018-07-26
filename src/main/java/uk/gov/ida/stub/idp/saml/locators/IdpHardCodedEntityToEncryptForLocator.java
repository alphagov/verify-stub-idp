package uk.gov.ida.stub.idp.saml.locators;

import uk.gov.ida.saml.security.EntityToEncryptForLocator;

import javax.inject.Inject;
import javax.inject.Named;

public class IdpHardCodedEntityToEncryptForLocator implements EntityToEncryptForLocator {

    private final String hubEntityId;

    @Inject
    public IdpHardCodedEntityToEncryptForLocator(@Named("HubEntityId") String hubEntityId) {
        this.hubEntityId = hubEntityId;
    }

    @Override
    public String fromRequestId(String requestId) {
        return hubEntityId;
    }
}

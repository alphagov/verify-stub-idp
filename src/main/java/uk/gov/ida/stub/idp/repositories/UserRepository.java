package uk.gov.ida.stub.idp.repositories;

import uk.gov.ida.stub.idp.domain.IdpUser;

import java.util.Collection;

public interface UserRepository {
    Collection<IdpUser> getUsersForIdp(String idpFriendlyName);
    void addOrUpdateUserForIdp(String idpFriendlyName, IdpUser user);
    void deleteUserFromIdp(String idpFriendlyName, String username);
}

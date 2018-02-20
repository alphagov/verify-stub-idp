package uk.gov.ida.stub.idp.repositories.jdbc;

import uk.gov.ida.stub.idp.domain.IdpUser;
import uk.gov.ida.stub.idp.repositories.UserRepository;

import javax.inject.Inject;
import java.util.Collection;

public class JDBIUserRepository implements UserRepository {

    @Inject
    public JDBIUserRepository() {
    }

    @Override
    public Collection<IdpUser> getUsersForIdp(String idpFriendlyName) {
        return null;
    }

    @Override
    public void addOrUpdateUserForIdp(String idpFriendlyName, IdpUser user) {

    }

    @Override
    public void deleteUserFromIdp(String idpFriendlyName, String username) {

    }
}

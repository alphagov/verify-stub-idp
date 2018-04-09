package uk.gov.ida.stub.idp.repositories.infinispan;

import uk.gov.ida.stub.idp.domain.IdpUser;
import uk.gov.ida.stub.idp.repositories.UserRepository;

import javax.inject.Inject;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapUserRepository implements UserRepository {

    private final ConcurrentMap<String, ConcurrentMap<String, IdpUser>> mapOfIdpsAndUserMaps;

    @Inject
    public MapUserRepository(ConcurrentMap<String, ConcurrentMap<String, IdpUser>> mapOfIdpsAndUserMaps) {
        this.mapOfIdpsAndUserMaps = mapOfIdpsAndUserMaps;
    }

    @Override
    public Collection<IdpUser> getUsersForIdp(String idpFriendlyName) {
        return getUserMapForIdp(idpFriendlyName).values();
    }

    private synchronized ConcurrentMap<String, IdpUser> getUserMapForIdp(String idpFriendlyName) {
        return mapOfIdpsAndUserMaps.computeIfAbsent(idpFriendlyName, k -> new ConcurrentHashMap<>());
    }

    @Override
    public void addOrUpdateUserForIdp(String idpFriendlyName, IdpUser user) {
        ConcurrentMap<String, IdpUser> idpUsers = getUserMapForIdp(idpFriendlyName);
        idpUsers.put(user.getUsername(), user);
        mapOfIdpsAndUserMaps.put(idpFriendlyName, idpUsers);
    }

    @Override
    public void deleteUserFromIdp(String idpFriendlyName, String username) {
        ConcurrentMap<String, IdpUser> idpUsers = getUserMapForIdp(idpFriendlyName);
        idpUsers.remove(username);
        mapOfIdpsAndUserMaps.put(idpFriendlyName, idpUsers);
    }
}

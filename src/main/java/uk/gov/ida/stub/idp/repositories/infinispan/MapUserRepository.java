package uk.gov.ida.stub.idp.repositories.infinispan;

import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.domain.IdpUser;
import uk.gov.ida.stub.idp.repositories.UserRepository;

import javax.inject.Inject;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class MapUserRepository implements UserRepository {

    private final ConcurrentMap<String, ConcurrentMap<String, IdpUser>> mapOfIdpsAndUserMaps;

    @Inject
    public MapUserRepository(ConcurrentMap<String, ConcurrentMap<String, IdpUser>> mapOfIdpsAndUserMaps) {
        this.mapOfIdpsAndUserMaps = mapOfIdpsAndUserMaps;
    }

    @Override
    public Collection<DatabaseIdpUser> getUsersForIdp(String idpFriendlyName) {
        return getUserMapForIdp(idpFriendlyName).values().stream().map(DatabaseIdpUser::fromInfinispanUser).collect(Collectors.toList());
    }

    private synchronized ConcurrentMap<String, IdpUser> getUserMapForIdp(String idpFriendlyName) {
        return mapOfIdpsAndUserMaps.computeIfAbsent(idpFriendlyName, k -> new ConcurrentHashMap<>());
    }

    @Override
    public void addOrUpdateUserForIdp(String idpFriendlyName, DatabaseIdpUser user) {
        ConcurrentMap<String, IdpUser> idpUsers = getUserMapForIdp(idpFriendlyName);
        idpUsers.put(user.getUsername(), IdpUser.fromDatabaseUser(user));
        mapOfIdpsAndUserMaps.put(idpFriendlyName, idpUsers);
    }

    @Override
    public void deleteUserFromIdp(String idpFriendlyName, String username) {
        ConcurrentMap<String, IdpUser> idpUsers = getUserMapForIdp(idpFriendlyName);
        idpUsers.remove(username);
        mapOfIdpsAndUserMaps.put(idpFriendlyName, idpUsers);
    }
}

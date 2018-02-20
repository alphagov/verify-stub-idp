package uk.gov.ida.stub.idp.repositories;

import com.google.common.base.Optional;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.stub.idp.domain.IdpUser;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Optional.fromNullable;

public class AllIdpsUserRepository {

    private final UserRepository userRepository;
    private static final Logger LOG = LoggerFactory.getLogger(AllIdpsUserRepository.class);

    @Inject
    public AllIdpsUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createHardcodedTestUsersForIdp(String idpFriendlyId, String assetId){
        LOG.debug("Creating hard coded users for IDP: " + idpFriendlyId);
        List<IdpUser> sacredUsers = HardCodedTestUserList.getHardCodedTestUsers(assetId);

        for (IdpUser sacredUser : sacredUsers) {
            addUserForIdp(idpFriendlyId, sacredUser);
        }
    }

    public IdpUser createUserForIdp(String idpFriendlyName,
                                    String persistentId,
                                    List<MatchingDatasetValue<String>> firstnames,
                                    List<MatchingDatasetValue<String>> middleNames,
                                    List<MatchingDatasetValue<String>> surnames,
                                    Optional<MatchingDatasetValue<Gender>> gender,
                                    List<MatchingDatasetValue<LocalDate>> dateOfBirths,
                                    List<Address> addresses,
                                    String username,
                                    String password,
                                    AuthnContext levelOfAssurance) {


        IdpUser user = new IdpUser(
                username,
                persistentId,
                password,
                firstnames,
                middleNames,
                surnames,
                gender,
                dateOfBirths,
                addresses,
                levelOfAssurance);

        addUserForIdp(idpFriendlyName, user);

        return user;
    }

    public Collection<IdpUser> getAllUsersForIdp(String idpFriendlyName) {
        return userRepository.getUsersForIdp(idpFriendlyName);
    }

    public Optional<IdpUser> getUserForIdp(String idpFriendlyName, String username) {
        final List<IdpUser> matchingUsers = userRepository.getUsersForIdp(idpFriendlyName)
                .stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .collect(Collectors.toList());
        if (!matchingUsers.isEmpty()) {
            return fromNullable(matchingUsers.get(0));
        }
        return Optional.absent();
    }

    public boolean containsUserForIdp(String idpFriendlyName, String username) {
        return getUserForIdp(idpFriendlyName, username).isPresent();
    }

    private void addUserForIdp(String idpFriendlyName, IdpUser user) {
        LOG.debug("Creating user " + user.getUsername() + " for IDP " + idpFriendlyName);
        userRepository.addOrUpdateUserForIdp(idpFriendlyName, user);
   }

    public void deleteUserFromIdp(String idpFriendlyName, String username) {
        LOG.debug("Deleting user " + username + " from IDP " + idpFriendlyName);
        userRepository.deleteUserFromIdp(idpFriendlyName, username);
    }
}

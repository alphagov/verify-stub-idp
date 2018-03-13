package uk.gov.ida.stub.idp.services;

import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.dtos.IdpUserDto;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.validation.ValidationResponse;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.PathParam;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserService {

    public class ResponseMessage {
        private String message;

        private ResponseMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private final IdpStubsRepository idpStubsRepository;

    @Inject
    public UserService(IdpStubsRepository idpStubsRepository) {
        this.idpStubsRepository = idpStubsRepository;
    }

    public Optional<IdpUserDto> getUser(String idpName, String username) {
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        Optional<DatabaseIdpUser> user = idp.getUser(username);

        if (user.isPresent()) {
            return Optional.of(transform(user.get()));
        }
        return Optional.empty();
    }


    public ResponseMessage createUsers(@PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName, @NotNull IdpUserDto[] users) {
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);

        createIdpUsers(idp, users);

        return new ResponseMessage("Users created.");
    }

    public ResponseMessage deleteUser(@PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName, @NotNull IdpUserDto userToDelete) {
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);

        deleteIdpUser(idp, userToDelete);

        return new ResponseMessage("User " + userToDelete.getUsername() + " deleted.");
    }

    public Collection<IdpUserDto> getIdpUserDtos(@PathParam(Urls.IDP_ID_PARAM) String idpName) {
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        return idp.getAllUsers().stream().map(this::transform).collect(Collectors.toList());
    }

    public List<ValidationResponse> validateUsers(List<IdpUserDto> users) {
        List<ValidationResponse> validationResponses = new ArrayList<>();
        for (IdpUserDto user : users) {
            validationResponses.add(validateUser(user));
        }
        return validationResponses;
    }

    private ValidationResponse validateUser(IdpUserDto user) {
        List<String> messages = new ArrayList<>();
        if (user.getLevelOfAssurance() == null) {
            messages.add("Level of Assurance was not specified.");
        } else {
            try {
                AuthnContext.valueOf(user.getLevelOfAssurance());
            } catch (IllegalArgumentException e) {
                messages.add(user.getLevelOfAssurance() + ": Level of Assurance supplied was not valid.");
            }
        }

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            messages.add("Username was not specified or was empty.");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            messages.add("Password was not specified or was empty.");
        }

        if (messages.isEmpty()) {
            return ValidationResponse.aValidResponse();
        } else {
            return ValidationResponse.anInvalidResponse(messages);
        }
    }

    private void createIdpUsers(Idp idp, IdpUserDto[] users) {
        for (IdpUserDto user : users) {
            createIdpUser(idp, user);
        }
    }

    private void createIdpUser(Idp idp, IdpUserDto user) {
        idp.createUser(
                user.getPid(),
                createListOfValues(user.getFirstName()),
                createListOfValues(user.getMiddleNames()),
                user.getSurnames(),
                user.getGender(),
                createListOfValues(user.getDateOfBirth()),
                createListOfValues(user.getAddress()),
                user.getUsername(),
                user.getPassword(),
                AuthnContext.valueOf(user.getLevelOfAssurance())
        );
    }

    private void deleteIdpUser(Idp idp, IdpUserDto userToDelete) {
        idp.deleteUser(userToDelete.getUsername());
    }

    private <T> List<T> createListOfValues(Optional<T> value) {
        if (value.isPresent()) {
            return Collections.singletonList(value.get());
        }
        return Collections.emptyList();
    }

    private IdpUserDto transform(DatabaseIdpUser idpUser) {
        return IdpUserDto.fromIdpUser(idpUser);
    }
}

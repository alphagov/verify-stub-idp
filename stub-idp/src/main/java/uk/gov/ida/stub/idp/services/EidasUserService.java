package uk.gov.ida.stub.idp.services;

import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.domain.DatabaseEidasUser;
import uk.gov.ida.stub.idp.domain.EidasScheme;
import uk.gov.ida.stub.idp.dtos.EidasUserDto;
import uk.gov.ida.stub.idp.exceptions.InvalidEidasSchemeException;
import uk.gov.ida.stub.idp.repositories.StubCountry;
import uk.gov.ida.stub.idp.repositories.StubCountryRepository;
import uk.gov.ida.stub.idp.validation.ValidationResponse;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.PathParam;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EidasUserService {

    public class ResponseMessage {
        private String message;

        private ResponseMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private final StubCountryRepository stubCountryRepository;

    @Inject
    public EidasUserService(StubCountryRepository stubCountryRepository) {
        this.stubCountryRepository = stubCountryRepository;
    }

    public Optional<EidasUserDto> getUser(String schemeName, String username) {
        return getStubCountryForSchemeName(schemeName).getUser(username).map(this::transform);
    }

    public ResponseMessage createUsers(@PathParam(Urls.SCHEME_ID_PARAM) @NotNull String schemeName, @NotNull EidasUserDto[] users) {
        final StubCountry stubCountry = getStubCountryForSchemeName(schemeName);

        createEidasUsers(stubCountry, users);

        return new ResponseMessage("Users created.");
    }

    public ResponseMessage deleteUser(@PathParam(Urls.SCHEME_ID_PARAM) @NotNull String schemeName, @NotNull EidasUserDto userToDelete) {
        final StubCountry stubCountry = getStubCountryForSchemeName(schemeName);

        deleteEidasUser(stubCountry, userToDelete);

        return new ResponseMessage("User " + userToDelete.getUsername() + " deleted.");
    }

    public Collection<EidasUserDto> getIdpUserDtos(@PathParam(Urls.SCHEME_ID_PARAM) String schemeName) {
        final StubCountry stubCountry = getStubCountryForSchemeName(schemeName);
        return stubCountry.getAllUsers().stream().map(this::transform).collect(Collectors.toList());
    }

    public List<ValidationResponse> validateUsers(List<EidasUserDto> users) {
        List<ValidationResponse> validationResponses = new ArrayList<>();
        for (EidasUserDto user : users) {
            validationResponses.add(validateUser(user));
        }
        return validationResponses;
    }

    private ValidationResponse validateUser(EidasUserDto user) {
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

    private void createEidasUsers(StubCountry stubCountry, EidasUserDto[] users) {
        for (EidasUserDto user : users) {
            createEidasUser(stubCountry, user);
        }
    }

    private void createEidasUser(StubCountry stubCountry, EidasUserDto user) {
        stubCountry.createUser(
                user.getUsername(),
                user.getPassword(),
                user.getFirstName(),
                user.getFirstNameNonLatin(),
                user.getFamilyName(),
                user.getFamilyNameNonLatin(),
                user.getDateOfBirth(),
                AuthnContext.valueOf(user.getLevelOfAssurance())
        );
    }

    private void deleteEidasUser(StubCountry stubCountry, EidasUserDto userToDelete) {
        stubCountry.deleteUser(userToDelete.getUsername());
    }

    private EidasUserDto transform(DatabaseEidasUser eidasUser) {
        return EidasUserDto.fromEidasUser(eidasUser);
    }

    private StubCountry getStubCountryForSchemeName(String schemeName) {
        final Optional<EidasScheme> eidasScheme = EidasScheme.fromString(schemeName);

        if(!eidasScheme.isPresent()) {
            throw new InvalidEidasSchemeException();
        }

        return stubCountryRepository.getStubCountryWithFriendlyId(eidasScheme.get());
    }
}

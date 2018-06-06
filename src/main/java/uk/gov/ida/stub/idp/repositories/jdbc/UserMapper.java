package uk.gov.ida.stub.idp.repositories.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.ida.stub.idp.domain.DatabaseEidasUser;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.repositories.jdbc.json.EidasUserJson;
import uk.gov.ida.stub.idp.repositories.jdbc.json.IdpUserJson;

import javax.inject.Singleton;

import static uk.gov.ida.stub.idp.utils.Exceptions.uncheck;

@Singleton
public class UserMapper {

    private final ObjectMapper mapper;

    public UserMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public User mapFrom(String idpFriendlyName, DatabaseIdpUser idpUser) {
        String idpUserAsJson = uncheck(() -> mapper.writeValueAsString(idpUser));

        return new User(
            null,
            idpUser.getUsername(),
            idpUser.getPassword(),
            idpFriendlyName,
            idpUserAsJson
        );
    }

    public User mapFrom(String stubCountryFriendlyName, DatabaseEidasUser eidasUser) {
        String eidasUserAsJson = uncheck(() -> mapper.writeValueAsString(eidasUser));

        return new User(
                null,
                eidasUser.getUsername(),
                eidasUser.getPassword(),
                stubCountryFriendlyName,
                eidasUserAsJson
        );
    }

    public DatabaseIdpUser mapToIdpUser(User user) {
        IdpUserJson idpUserJson = uncheck(() -> mapper.readValue(user.getData(), IdpUserJson.class));

        return new DatabaseIdpUser(
            idpUserJson.getUsername(),
            idpUserJson.getPersistentId(),
            idpUserJson.getPassword(),
            idpUserJson.getFirstnames(),
            idpUserJson.getMiddleNames(),
            idpUserJson.getSurnames(),
            idpUserJson.getGender(),
            idpUserJson.getDateOfBirths(),
            idpUserJson.getAddresses(),
            idpUserJson.getLevelOfAssurance()
        );
    }

    public DatabaseEidasUser mapToEidasUser(User user) {
        EidasUserJson eidasUserJson = uncheck(() -> mapper.readValue(user.getData(), EidasUserJson.class));

        return new DatabaseEidasUser(
                eidasUserJson.getUsername(),
                eidasUserJson.getPersistentId(),
                eidasUserJson.getPassword(),
                eidasUserJson.getFirstname(),
                eidasUserJson.getNonLatinFirstname(),
                eidasUserJson.getSurname(),
                eidasUserJson.getNonLatinSurname(),
                eidasUserJson.getDateOfBirth(),
                eidasUserJson.getLevelOfAssurance()
        );
    }
}

package uk.gov.ida.stub.idp.repositories.jdbc;

import org.jdbi.v3.core.Jdbi;
import uk.gov.ida.stub.idp.repositories.TokenRepository;

import javax.inject.Inject;
import java.util.Optional;

public class JDBITokenRepository implements TokenRepository {
    private final Jdbi jdbi;

    @Inject
    public JDBITokenRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public Optional<String> getIdToken(String authCode) {
        //TODO this table structure is not the best...

        Optional<String> idToken = jdbi.withHandle(handle ->
                handle.createQuery("select id_token from token where auth_code = :accessCode")
                        .bind("accessCode", authCode)
                        .mapTo(String.class)
                        .findFirst());

        return idToken;
    }

    @Override
    public Optional<String> getAccessToken(String authCode) {
        //TODO this table structure is not the best...

        Optional<String> accessToken = jdbi.withHandle(handle ->
                handle.createQuery("select access_token from token where auth_code = :authCode")
                        .bind("authCode", authCode)
                        .mapTo(String.class)
                        .findFirst());

        return accessToken;
    }

    @Override
    public void storeTokens(String idToken, String accessToken, String authCode) {
        jdbi.withHandle(handle ->
                handle.createUpdate("insert into token(auth_code, access_token, id_token) values (:authCode, :accessToken, :idToken)")
                        .bind("authCode", authCode)
                        .bind("accessToken", accessToken)
                        .bind("idToken", idToken)
                        .execute());
    }
}

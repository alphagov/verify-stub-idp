package uk.gov.ida.stub.idp.repositories;

import java.util.Optional;

public interface TokenRepository {
    Optional<String> getIdToken(String authCode);
    Optional<String> getAccessToken(String accessCode);
    void storeTokens(String idToken, String accessToken, String authCode);
}

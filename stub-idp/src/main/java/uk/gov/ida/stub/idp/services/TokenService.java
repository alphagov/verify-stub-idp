package uk.gov.ida.stub.idp.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import uk.gov.ida.stub.idp.repositories.TokenRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

public class TokenService {
    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public AuthorizationCode generateTokensAndGetAuthCode() {
        RSAKey signingKey = createSigningKey();

        IDTokenClaimsSet idTokenClaimsSet = createIdTokenClaimsSet();
        SignedJWT signedIdToken = getSignedIdToken(idTokenClaimsSet, signingKey);
        AuthorizationCode authCode = createAuthorizationCode();
        AccessToken accessToken = createAccessToken();

        tokenRepository.storeTokens(signedIdToken.serialize(), accessToken.getValue(), authCode.getValue());
        return authCode;
    }

    public OIDCTokens getTokens(AuthorizationCode authCode) {
        Optional<AccessToken> accessToken = tokenRepository.getAccessToken(authCode.getValue()).map(BearerAccessToken::new);
        Optional<SignedJWT> idToken = tokenRepository.getIdToken(authCode.getValue()).map(str -> {
            try {
                return SignedJWT.parse(str);
            } catch (java.text.ParseException e) {
                // TODO handle exceptions
                throw new RuntimeException(e);
            }
        });

        if (idToken.isPresent() && accessToken.isPresent()) {
            // TODO implement refresh token
            return new OIDCTokens(idToken.get(), accessToken.get(), null);
        } else {
            // TODO handle exceptions
            throw new RuntimeException();
        }
    }

    private IDTokenClaimsSet createIdTokenClaimsSet() {
        //TODO create ID Token with real values
        return new IDTokenClaimsSet(
                new Issuer("iss"),
                new Subject("sub"),
                Arrays.asList(new Audience("aud")),
                new Date(),
                new Date());
    }

    private RSAKey createSigningKey() {
        // TODO use existing stub-idp signing key
        try {
            return new RSAKeyGenerator(2048).keyID("123").generate();
        } catch (JOSEException e) {
            // TODO handle exceptions
            throw new RuntimeException(e);
        }
    }

    private SignedJWT getSignedIdToken(IDTokenClaimsSet idTokenClaimsSet, RSAKey signingKey) {
        try {
            JWSSigner signer = new RSASSASigner(signingKey);

            SignedJWT idToken = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(signingKey.getKeyID()).build(),
                    idTokenClaimsSet.toJWTClaimsSet());

            idToken.sign(signer);
            return idToken;
        } catch (JOSEException | ParseException e) {
            // TODO handle exceptions
            throw new RuntimeException(e);
        }
    }

    private AuthorizationCode createAuthorizationCode() {
        //TODO is this ok?
        return new AuthorizationCode();
    }

    private AccessToken createAccessToken() {
        // TODO is this ok?
        return new BearerAccessToken();
    }
}

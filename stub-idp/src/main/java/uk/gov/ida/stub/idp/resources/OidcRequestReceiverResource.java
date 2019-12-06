package uk.gov.ida.stub.idp.resources;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.services.TokenService;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Path("/{"+Urls.IDP_ID_PARAM+"}")
public class OidcRequestReceiverResource {
    private final TokenService tokenService;

    @Inject
    public OidcRequestReceiverResource(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GET
    @Path("/authorize")
    @Produces(MediaType.TEXT_HTML)
    public Response authorize(
            @Context UriInfo uriInfo,
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName) {
        URI uri = uriInfo.getRequestUri();
        try {
            AuthenticationRequest authenticationRequest = AuthenticationRequest.parse(uri);

            // TODO implement this validation
            validateAuthenticationRequest(authenticationRequest);

            // TODO is this the right time to create and store tokens?
            AuthorizationCode authorizationCode = tokenService.generateTokensAndGetAuthCode();

            // TODO should we return a JWT secured response? What should the nulls be?
            AuthenticationSuccessResponse successResponse =
                    new AuthenticationSuccessResponse(authenticationRequest.getRedirectionURI(),
                            authorizationCode,
                            null,
                            null,
                            authenticationRequest.getState(),
                            null,
                            null);

            return Response.status(302).location(successResponse.toURI()).build();
        } catch (ParseException e) {
            // TODO handle exceptions
            throw new RuntimeException(e);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/token")
    public Response getToken(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @FormParam("code") @NotNull AuthorizationCode authCode) {
        OIDCTokenResponse response = new OIDCTokenResponse(tokenService.getTokens(authCode));
        return Response.ok(response.toJSONObject()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/userinfo")
    public Response getUserInfo(@HeaderParam("Authorization") @NotNull String authorizationHeader) {
        try {
            AccessToken accessToken = AccessToken.parse(authorizationHeader);
            UserInfo userInfo = getUserInfo(accessToken);

            return Response.ok(userInfo.toJSONObject()).build();
        } catch (ParseException e) {
            // TODO handle exceptions
            throw new RuntimeException(e);
        }
    }

    private void validateAuthenticationRequest(AuthenticationRequest authenticationRequest) {
        // TODO implement
    }

    private UserInfo getUserInfo(AccessToken accessToken) {
        // TODO use access token to get actual user info
        return new UserInfo(new Subject("sub"));
    }
}

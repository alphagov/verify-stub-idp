package uk.gov.ida.stub.idp.resources.eidas;

import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.dtos.EidasUserDto;
import uk.gov.ida.stub.idp.exceptions.IdpUserNotFoundException;
import uk.gov.ida.stub.idp.services.EidasUserService;
import uk.gov.ida.stub.idp.validation.ValidationResponse;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;

@Path(Urls.EIDAS_USERS_RESOURCE)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EidasUserResource {

    private final EidasUserService userService;

    @Inject
    public EidasUserResource(EidasUserService userService) {
        this.userService = userService;
    }

    @GET
    public Collection<EidasUserDto> getAllUsers(@PathParam(Urls.SCHEME_ID_PARAM) String schemeName) {
        return userService.getIdpUserDtos(schemeName);
    }

    @GET
    @Path(Urls.GET_USER_PATH)
    public EidasUserDto getUser(
            @PathParam(Urls.SCHEME_ID_PARAM) @NotNull String schemeName,
            @PathParam(Urls.USERNAME_PARAM) @NotNull String username) {

        Optional<EidasUserDto> user = userService.getUser(schemeName, username);
        if (user.isPresent()) return user.get();

        throw new IdpUserNotFoundException(format("user not found: {0}", username));
    }

    @POST
    public Response createUsers(
            @PathParam(Urls.SCHEME_ID_PARAM) @NotNull String schemeName,
            @NotNull EidasUserDto... users) {

        List<ValidationResponse> validationResponses = userService.validateUsers(asList(users));

        if(validationResponses.stream().anyMatch(validationResponse -> !validationResponse.isOk())) {
            final List<String> validationMessages = validationResponses.stream()
                    .filter(validationResponse -> !validationResponse.isOk())
                    .map(ValidationResponse::getMessages)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            return Response.status(Response.Status.BAD_REQUEST).entity(validationMessages).build();
        }

        EidasUserService.ResponseMessage responseMessage = userService.createUsers(schemeName, users);

        return Response.created(UriBuilder.fromPath("").build())
                .entity(Entity.json(responseMessage))
                .build();
    }

    @POST
    @Path(Urls.DELETE_USER_PATH)
    public Response deleteUser(
            @PathParam(Urls.SCHEME_ID_PARAM) @NotNull String schemeName,
            @NotNull EidasUserDto userToDelete) {
        final EidasUserService.ResponseMessage responseMessage = userService.deleteUser(schemeName, userToDelete);

        return Response.ok(UriBuilder.fromPath("").build())
                .entity(Entity.json(responseMessage))
                .build();
    }
}

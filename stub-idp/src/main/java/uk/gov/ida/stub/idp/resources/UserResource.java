package uk.gov.ida.stub.idp.resources;

import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.dtos.IdpUserDto;
import uk.gov.ida.stub.idp.exceptions.IdpUserNotFoundException;
import uk.gov.ida.stub.idp.services.UserService;
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

@Path(Urls.USERS_RESOURCE)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserService userService;

    @Inject
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GET
    public Collection<IdpUserDto> getAllUsers(@PathParam(Urls.IDP_ID_PARAM) String idpName) {
        return userService.getIdpUserDtos(idpName);
    }

    @GET
    @Path(Urls.GET_USER_PATH)
    public IdpUserDto getUser(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @PathParam(Urls.USERNAME_PARAM) @NotNull String username) {

        Optional<IdpUserDto> user = userService.getUser(idpName, username);
        if (user.isPresent()) return user.get();

        throw new IdpUserNotFoundException(format("user not found: {0}", username));
    }

    @POST
    public Response createUsers(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @NotNull IdpUserDto... users) {

        List<ValidationResponse> validationResponses = userService.validateUsers(asList(users));

        if(validationResponses.stream().anyMatch(ValidationResponse::isNotOk)) {
            final List<String> validationMessages = validationResponses.stream()
                    .filter(ValidationResponse::isNotOk)
                    .map(ValidationResponse::getMessages)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            return Response.status(Response.Status.BAD_REQUEST).entity(validationMessages).build();
        }

        UserService.ResponseMessage responseMessage = userService.createUsers(idpName, users);

        return Response.created(UriBuilder.fromPath("").build())
                .entity(Entity.json(responseMessage))
                .build();
    }

    @POST
    @Path(Urls.DELETE_USER_PATH)
    public Response deleteUser(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @NotNull IdpUserDto userToDelete) {
        final UserService.ResponseMessage responseMessage = userService.deleteUser(idpName, userToDelete);

        return Response.ok(UriBuilder.fromPath("").build())
                .entity(Entity.json(responseMessage))
                .build();
    }

}

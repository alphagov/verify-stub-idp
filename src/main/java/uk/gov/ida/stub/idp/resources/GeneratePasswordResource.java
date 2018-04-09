package uk.gov.ida.stub.idp.resources;

import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.services.GeneratePasswordService;
import uk.gov.ida.stub.idp.views.GeneratePasswordView;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(Urls.PASSWORD_GEN_RESOURCE)
@Produces(MediaType.TEXT_HTML)
public class GeneratePasswordResource {

    private final GeneratePasswordService generatePasswordService;

    @Inject
    public GeneratePasswordResource(GeneratePasswordService generatePasswordService) {
        this.generatePasswordService = generatePasswordService;
    }

    @GET
    public GeneratePasswordView getPasswordPage() {
        String candidatePassword = generatePasswordService.generateCandidatePassword();
        String hash = generatePasswordService.getHashedPassword(candidatePassword);

        return new GeneratePasswordView(candidatePassword, hash, "Generate Password Hash");
    }

}

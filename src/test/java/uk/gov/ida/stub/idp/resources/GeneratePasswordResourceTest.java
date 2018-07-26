package uk.gov.ida.stub.idp.resources;

import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;
import uk.gov.ida.stub.idp.services.GeneratePasswordService;
import uk.gov.ida.stub.idp.views.GeneratePasswordView;

import static org.assertj.core.api.Assertions.assertThat;

public class GeneratePasswordResourceTest {

    @Test
    public void saltedPasswordsShouldVerifyWithBCrypt() throws Exception {
        GeneratePasswordResource generatePasswordResource = new GeneratePasswordResource(new GeneratePasswordService());

        GeneratePasswordView passwordPage = generatePasswordResource.getPasswordPage();

        assertThat(BCrypt.checkpw(passwordPage.getPassword(), passwordPage.getPasswordHash())).isTrue();
    }

}

package uk.gov.ida.stub.idp.services;

import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.assertj.core.api.Assertions.assertThat;

public class GeneratePasswordServiceTest {

    @Test
    public void saltedPasswordsShouldVerifyWithBCrypt() throws Exception {
        GeneratePasswordService generatePasswordService = new GeneratePasswordService();

        final String password = generatePasswordService.generateCandidatePassword();
        final String hash = generatePasswordService.getHashedPassword(password);

        assertThat(BCrypt.checkpw(password, hash)).isTrue();
    }

}
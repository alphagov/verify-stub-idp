package uk.gov.ida.stub.idp.services;

import org.mindrot.jbcrypt.BCrypt;

import javax.inject.Inject;
import java.util.UUID;

public class GeneratePasswordService {

    @Inject
    public GeneratePasswordService() {
    }

    public String getHashedPassword(String candidatePassword) {
        return BCrypt.hashpw(candidatePassword, BCrypt.gensalt());
    }

    public String generateCandidatePassword() {
        String rawguid = UUID.randomUUID().toString();
        return rawguid.replace("-","").substring(0, 24);
    }

}

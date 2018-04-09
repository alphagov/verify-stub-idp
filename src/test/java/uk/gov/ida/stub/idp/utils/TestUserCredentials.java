package uk.gov.ida.stub.idp.utils;

import org.mindrot.jbcrypt.BCrypt;
import uk.gov.ida.stub.idp.configuration.UserCredentials;

public class TestUserCredentials extends UserCredentials {

    public TestUserCredentials(String user, String password) {
        this.user = user;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }
}

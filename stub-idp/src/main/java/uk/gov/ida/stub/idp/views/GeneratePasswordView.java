package uk.gov.ida.stub.idp.views;

import io.dropwizard.views.View;

public class GeneratePasswordView extends View {

    private final String password;
    private final String passwordHash;
    private String pageTitle;

    public GeneratePasswordView(String password, String passwordHash, String pageTitle) {
        super("generatePassword.ftl");
        this.password = password;
        this.passwordHash = passwordHash;
        this.pageTitle = pageTitle;
    }

    public String getPassword(){
        return password;
    }

    public String getPasswordHash(){
        return passwordHash;
    }

    public String getPageTitle() {
        return pageTitle;
    }
}

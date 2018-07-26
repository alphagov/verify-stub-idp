package uk.gov.ida.stub.idp.repositories.jdbc;

public class User {

    private final Integer id;
    private final String username;
    private final String password;
    private final String idpFriendlyId;
    private final String data;

    public User(Integer id, String username, String password, String idpFriendlyId, String data) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.idpFriendlyId = idpFriendlyId;
        this.data = data;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getIdpFriendlyId() {
        return idpFriendlyId;
    }

    public String getData() {
        return data;
    }
}

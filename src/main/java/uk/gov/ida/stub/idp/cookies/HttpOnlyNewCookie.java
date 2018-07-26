package uk.gov.ida.stub.idp.cookies;

import javax.ws.rs.core.NewCookie;

public class HttpOnlyNewCookie extends NewCookie {

    public HttpOnlyNewCookie(String name, String value, String path, String comment, int maxAge, boolean secure) {
        super(name, value, path, null, comment, maxAge, secure);
    }

    @Override
    public String toString() {
        return super.toString() + "; HttpOnly;";
    }
}

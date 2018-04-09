package uk.gov.ida.stub.idp.auth;

import com.google.common.base.Splitter;
import org.mindrot.jbcrypt.BCrypt;
import uk.gov.ida.shared.utils.string.StringEncoding;
import uk.gov.ida.stub.idp.configuration.UserCredentials;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class UserResourceBasicAuthFilter implements Filter {
    private final IdpStubsRepository idpStubsRepository;
    private Splitter splitter = Splitter.on(':').limit(2);
    private int credOffset = "Basic ".length();

    public UserResourceBasicAuthFilter(IdpStubsRepository idpStubsRepository) {
        this.idpStubsRepository = idpStubsRepository;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        UsernamePassword usernamePasswordFromRequest = getUsernamePasswordFromRequest(httpServletRequest);

        // Get the friendly id
        String[] requestSegments = ((HttpServletRequest) request).getRequestURI().split("/");
        if(isUrlWhichNeedsBasicAuth(requestSegments)) {
            String friendlyId = requestSegments[1];

            List<UserCredentials> userCredentialsList = idpStubsRepository.getUserCredentialsForFriendlyId(friendlyId);

            for (UserCredentials userCredentials : userCredentialsList) {
                if (requestAuthMatchesUsernameAndPassword(userCredentials, usernamePasswordFromRequest)) {
                    chain.doFilter(request, response);
                    return;
                }
            }
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.setHeader("WWW-Authenticate", "Basic realm=\"Admin\"");
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isUrlWhichNeedsBasicAuth(String[] requestSegments) {
        return requestSegments.length >= 3 && "users".equals(requestSegments[2]);
    }

    @Override
    public void destroy() {
        // this method intentionally left blank
    }

    private boolean requestAuthMatchesUsernameAndPassword(UserCredentials userCredentials, UsernamePassword userProvidedCredentials) {
        return (userCredentials.getUser().equalsIgnoreCase(userProvidedCredentials.getUsername())
                && BCrypt.checkpw(userProvidedCredentials.getPassword(), userCredentials.getPassword()));
    }

    private UsernamePassword getUsernamePasswordFromRequest(HttpServletRequest request) {
        UsernamePassword userProvidedCredentials;
        String providedPassword = null;
        String providedUserName = null;

        String basicAuthHeaderValue = request.getHeader("Authorization");
        if (basicAuthHeaderValue != null && basicAuthHeaderValue.startsWith("Basic ")) {
            String combined = StringEncoding.fromBase64Encoded(basicAuthHeaderValue.substring(credOffset));
            Iterator<String> credentials = splitter.split(combined).iterator();

            if (credentials.hasNext()) {
                providedUserName = credentials.next();
                if (credentials.hasNext()) {
                    providedPassword = credentials.next();
                }
            }
        }

        userProvidedCredentials = new UsernamePassword(providedPassword, providedUserName);
        return userProvidedCredentials;
    }

    private final class UsernamePassword {
        private String password;
        private String username;

        private UsernamePassword(String password, String username) {
            this.password = password;
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public String getUsername() {
            return username;
        }
    }
}

package uk.gov.ida.stub.idp.auth;

import com.google.common.base.Splitter;
import org.apache.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import uk.gov.ida.shared.utils.string.StringEncoding;
import uk.gov.ida.stub.idp.configuration.UserCredentials;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.repositories.StubCountryRepository;

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

import static java.text.MessageFormat.format;

public class UserResourceBasicAuthFilter implements Filter {

    private final Logger LOG = Logger.getLogger(UserResourceBasicAuthFilter.class);

    private final IdpStubsRepository idpStubsRepository;
    private final StubCountryRepository stubCountryRepository;
    private final Splitter splitter = Splitter.on(':').limit(2);
    private final int credOffset = "Basic ".length();

    public UserResourceBasicAuthFilter(IdpStubsRepository idpStubsRepository, StubCountryRepository stubCountryRepository) {
        this.idpStubsRepository = idpStubsRepository;
        this.stubCountryRepository = stubCountryRepository;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        //do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        UsernamePassword usernamePasswordFromRequest = getUsernamePasswordFromRequest(httpServletRequest);

        String[] requestSegments = ((HttpServletRequest) request).getRequestURI().split("/");
        String friendlyId = requestSegments[1];

        List<UserCredentials> userCredentialsList;
        if (isUrlWhichNeedsBasicAuth(requestSegments)) {
            userCredentialsList = idpStubsRepository.getUserCredentialsForFriendlyId(friendlyId);
        }
        else if (isEidasUrlWhichNeedsBasicAuth(requestSegments)) {
            userCredentialsList = stubCountryRepository.getUserCredentialsForFriendlyId(friendlyId);
        }
        else {
            chain.doFilter(request, response);
            return;
        }

        for (UserCredentials userCredentials : userCredentialsList) {
            if (requestAuthMatchesUsernameAndPassword(userCredentials, usernamePasswordFromRequest)) {
                LOG.info(format("Basic auth login success for IDP {0}, user {1}", friendlyId, usernamePasswordFromRequest.getUsername()));
                chain.doFilter(request, response);
                return;
            }
        }

        LOG.error(format("Basic auth login failure for IDP {0}, user {1}", friendlyId, usernamePasswordFromRequest.getUsername()));
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setHeader("WWW-Authenticate", "Basic realm=\"Admin\"");
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private boolean isUrlWhichNeedsBasicAuth(String[] requestSegments) {
        return requestSegments.length >= 3 && "users".equals(requestSegments[2]);
    }

    private boolean isEidasUrlWhichNeedsBasicAuth(String[] requestSegments) {
        return requestSegments.length >= 4 && "eidas".equals(requestSegments[1]) && "users".equals(requestSegments[3]);
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

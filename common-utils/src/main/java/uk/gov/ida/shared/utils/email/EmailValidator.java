package uk.gov.ida.shared.utils.email;

import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {

    private static final String SPECIAL_CHARS = "\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]";
    private static final String VALID_CHARS = "[^\\s" + SPECIAL_CHARS + "]";
    private static final String QUOTED_USER = "(\"[^\"]*\")";
    private static final String WORD = "((" + VALID_CHARS + "|')+|" + QUOTED_USER + ")";

    private static final String LEGAL_INTERNATIONAL_CHARS_REGEX = "^[\\p{ASCII}\\p{L}]+$";
    private static final String EMAIL_REGEX = "^\\s*?(.+)@(.+?)\\s*$";
    private static final String IP_DOMAIN_REGEX = "^\\[(.*)\\]$";
    private static final String USER_REGEX = "^\\s*" + WORD + "(\\." + WORD + ")*$";

    private static final Pattern MATCH_INTERNATIONAL_CHARS_PATTERN = Pattern.compile(LEGAL_INTERNATIONAL_CHARS_REGEX);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern IP_DOMAIN_PATTERN = Pattern.compile(IP_DOMAIN_REGEX);
    private static final Pattern USER_PATTERN = Pattern.compile(USER_REGEX);

    /**
     * <p>Checks if a field has a valid e-mail address.</p>
     *
     * @param email The value validation is being performed on.  A <code>null</code>
     *              value is considered invalid.
     * @return true if the email address is valid.
     */
    public static boolean isValid(String email) {
        if (email == null) {
            return false;
        }

        Matcher asciiMatcher = MATCH_INTERNATIONAL_CHARS_PATTERN.matcher(email);
        if (!asciiMatcher.matches()) {
            return false;
        }

        // Check the whole email address structure
        Matcher emailMatcher = EMAIL_PATTERN.matcher(email);
        if (!emailMatcher.matches()) {
            return false;
        }

        if (email.endsWith(".")) {
            return false;
        }

        if (!isValidUser(emailMatcher.group(1))) {
            return false;
        }

        if (!isValidDomain(emailMatcher.group(2))) {
            return false;
        }

        return true;
    }

    /**
     * Returns true if the domain component of an email address is valid.
     *
     * @param domain being validated.
     * @return true if the email address's domain is valid.
     */
    private static boolean isValidDomain(String domain) {
        // see if domain is an IP address in brackets
        Matcher ipDomainMatcher = IP_DOMAIN_PATTERN.matcher(domain);

        if (ipDomainMatcher.matches()) {
            InetAddressValidator inetAddressValidator =
                    InetAddressValidator.getInstance();
            return inetAddressValidator.isValid(ipDomainMatcher.group(1));
        } else {
            // Domain is symbolic name
            DomainValidator domainValidator =
                    DomainValidator.getInstance();
            return domainValidator.isValid(domain);
        }
    }

    /**
     * Returns true if the user component of an email address is valid.
     *
     * @param user being validated
     * @return true if the user name is valid.
     */
    private static boolean isValidUser(String user) {
        return USER_PATTERN.matcher(user).matches();
    }
}

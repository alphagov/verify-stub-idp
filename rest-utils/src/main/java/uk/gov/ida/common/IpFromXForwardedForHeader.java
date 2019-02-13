package uk.gov.ida.common;


import javax.servlet.http.HttpServletRequest;

public class IpFromXForwardedForHeader {

    public static final String IP_ADDRESS_NOT_PRESENT_VALUE = "<PRINCIPAL IP ADDRESS COULD NOT BE DETERMINED>";

    public String getPrincipalIpAddress(HttpServletRequest httpServletRequest) {
        final String xForwardedForHeaderValue = httpServletRequest.getHeader(HttpHeaders.X_FORWARDED_FOR);
        if (xForwardedForHeaderValue != null) {
            return xForwardedForHeaderValue;
        }

        return IP_ADDRESS_NOT_PRESENT_VALUE;
    }
}

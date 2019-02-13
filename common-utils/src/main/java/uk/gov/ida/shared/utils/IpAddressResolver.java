package uk.gov.ida.shared.utils;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

public class IpAddressResolver {

    public String lookupIpAddress(URI url) {
        try {
            return InetAddress.getByName(url.getHost()).getHostAddress();
        } catch (UnknownHostException e) {
            return "[Unable to resolve IP Address]";
        }
    }
}

package uk.gov.ida.configuration;

public interface SSLContextConfiguration {

    TrustedSslServersConfiguration getTrustedSslServers();

    MutualAuthConfiguration getMutualAuth();
}

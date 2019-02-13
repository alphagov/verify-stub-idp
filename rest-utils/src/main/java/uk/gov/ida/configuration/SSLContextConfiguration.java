package uk.gov.ida.configuration;

public interface SSLContextConfiguration {


    public TrustedSslServersConfiguration getTrustedSslServers();

    public MutualAuthConfiguration getMutualAuth();
}

package uk.gov.ida.restclient;

import com.google.common.base.Throwables;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;

public abstract class InsecureSSLSchemeRegistryBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(InsecureSSLSchemeRegistryBuilder.class);

    public static SchemeRegistry aConfigWithInsecureSSLSchemeRegistry(
            SSLContext sslContext, X509HostnameVerifier hostnameVerifier){

        final TrustManager[] trustManagers = getTrustManagers();
        try {
            sslContext.init(null, trustManagers, null);
        } catch (KeyManagementException e){
            LOG.error("Error when trying to create SSL.", e);
            throw Throwables.propagate(e);
        }

        final Scheme http = new Scheme("http", 80, new PlainSocketFactory());
        final Scheme https = new Scheme("https", 443, new SSLSocketFactory(sslContext, hostnameVerifier));
        final SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(http);
        schemeRegistry.register(https);
        return schemeRegistry;
    }

    private static TrustManager[] getTrustManagers() {
        return new TrustManager[]{new InsecureTrustManager()};
    }
}

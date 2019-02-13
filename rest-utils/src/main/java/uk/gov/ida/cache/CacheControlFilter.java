package uk.gov.ida.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class CacheControlFilter implements Filter {
    private static final String CACHE_CONTROL_KEY = org.apache.http.HttpHeaders.CACHE_CONTROL;
    private static final String CACHE_CONTROL_NO_CACHE_VALUE = "no-cache, no-store";
    private static final String PRAGMA_KEY = org.apache.http.HttpHeaders.PRAGMA;
    private static final String PRAGMA_NO_CACHE_VALUE = "no-cache";
    private static final String MAX_AGE = "max-age";

    private static final Logger LOG = LoggerFactory.getLogger(CacheControlFilter.class);

    private final AssetCacheConfiguration configuration;

    public CacheControlFilter(final AssetCacheConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(configuration.shouldCacheAssets() &&
                isCacheableAsset(((HttpServletRequest) request).getRequestURI())) {
            LOG.debug("Setting caching headers on " + ((HttpServletRequest) request).getServletPath());
            setCacheHeaders((HttpServletResponse) response);
        } else {
            LOG.debug("Setting no-cache headers on " + ((HttpServletRequest) request).getServletPath());
            setNoCacheHeaders((HttpServletResponse) response);
        }

        // This line must be called last (a bit of a broken api)
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Nothing to destroy
    }

    protected AssetCacheConfiguration getConfiguration() {
        return configuration;
    }

    protected abstract boolean isCacheableAsset(String localAddr);

    private void setCacheHeaders(final HttpServletResponse response) {
        response.setHeader(CACHE_CONTROL_KEY, MAX_AGE + "=" + configuration.getAssetsCacheDuration());
    }

    private void setNoCacheHeaders(final HttpServletResponse response) {
        response.setHeader(CACHE_CONTROL_KEY, CACHE_CONTROL_NO_CACHE_VALUE);
        response.setHeader(PRAGMA_KEY, PRAGMA_NO_CACHE_VALUE);
    }
}


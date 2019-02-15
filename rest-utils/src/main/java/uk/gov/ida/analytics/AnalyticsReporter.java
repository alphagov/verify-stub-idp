package uk.gov.ida.analytics;

import org.apache.http.client.utils.URIBuilder;
import org.glassfish.jersey.server.ContainerRequest;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.configuration.AnalyticsConfiguration;

import javax.inject.Inject;
import javax.ws.rs.core.Cookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class AnalyticsReporter {

    private static final Logger LOG = LoggerFactory.getLogger(AnalyticsReporter.class);
    public static final String PIWIK_VISITOR_ID = "PIWIK_VISITOR_ID";
    public static final String REFERER = "Referer";

    private AnalyticsConfiguration analyticsConfiguration;
    private PiwikClient piwikClient;

    @Inject
    public AnalyticsReporter(PiwikClient piwikClient, AnalyticsConfiguration analyticsConfiguration) {
        this.piwikClient = piwikClient;
        this.analyticsConfiguration = analyticsConfiguration;
    }

    public void reportCustomVariable(String friendlyDescription, ContainerRequest context, CustomVariable customVariable) {
        reportToPiwik(friendlyDescription, context, Optional.of(customVariable));
    }

    public void report(String friendlyDescription, ContainerRequest context) {
        reportToPiwik(friendlyDescription, context, Optional.<CustomVariable>empty());
    }

    public void reportPageView(String pageTitle, ContainerRequest context, String uri) {
        if(analyticsConfiguration.getEnabled()) {
            try {
                piwikClient.report(generateCustomURI(pageTitle, uri, getVisitorId(context)), context);
            } catch (Exception e) {
                LOG.error("Analytics Reporting error", e);
            }
        }
    }

    private Optional<String> getVisitorId(ContainerRequest context) {
        return Optional.ofNullable(context.getCookies().get(PIWIK_VISITOR_ID)).map(Cookie::getValue);
    }

    private URI generateCustomURI(String friendlyDescription, String url, Optional<String> visitorId) throws
            URISyntaxException {
        return buildAnalyticsURI(friendlyDescription, url, visitorId).build();
    }

    private URIBuilder buildBaseURI(Optional<String> visitorId) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(analyticsConfiguration.getPiwikServerSideUrl());
        if(visitorId.isPresent()) {
            uriBuilder.addParameter("_id", visitorId.get());
        }
        return uriBuilder
                .addParameter("idsite", analyticsConfiguration.getSiteId().toString())
                .addParameter("apiv", "1")
                .addParameter("rec", "1");
    }

    private URIBuilder buildAnalyticsURI(String friendlyDescription, String url, Optional<String> visitorId) throws
            URISyntaxException {
        return buildBaseURI(visitorId)
                .addParameter("action_name", friendlyDescription)
                .addParameter("url", url)
                .addParameter("cdt", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(DateTime.now()))
                .addParameter("cookie", "false");
    }

    URI generateURI(String friendlyDescription, ContainerRequest request, Optional<CustomVariable> customVariable, Optional<String> visitorId) throws URISyntaxException {
        URIBuilder uriBuilder = buildAnalyticsURI(friendlyDescription, request.getRequestUri().toString(), visitorId);
        customVariable.ifPresent(customVariable1 -> uriBuilder.addParameter("_cvar", customVariable1.getJson()));

        // Only FireFox on Windows is unable to provide referrer on AJAX calls
        Optional<String> refererHeader = Optional.ofNullable(request.getHeaderString(REFERER));
        if(refererHeader.isPresent()) {
            uriBuilder.addParameter("urlref", refererHeader.get());
            uriBuilder.addParameter("ref", refererHeader.get());
        }

        return uriBuilder.build();
    }

    private void reportToPiwik(String friendlyDescription, ContainerRequest context, Optional<CustomVariable> customVariable) {
        if (analyticsConfiguration.getEnabled()) {
            try {
                piwikClient.report(generateURI(friendlyDescription, context, customVariable, getVisitorId(context)), context);
            }
            catch(Exception e) {
                LOG.error("Analytics Reporting error", e);
            }
        }
    }
}

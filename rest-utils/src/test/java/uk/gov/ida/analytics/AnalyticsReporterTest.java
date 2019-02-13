package uk.gov.ida.analytics;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.glassfish.jersey.server.ContainerRequest;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ida.configuration.AnalyticsConfiguration;
import uk.gov.ida.configuration.AnalyticsConfigurationBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Cookie;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static com.google.common.base.Optional.fromNullable;
import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ida.analytics.AnalyticsReporter.PIWIK_VISITOR_ID;

@RunWith(MockitoJUnitRunner.class)
public class AnalyticsReporterTest {

    @Mock
    private ContainerRequest requestContext;

    @Mock
    private PiwikClient piwikClient;

    private String visitorId = "123";

    @Before
    public void setUp() throws Exception {
        doReturn(ImmutableMap.of(PIWIK_VISITOR_ID, new Cookie(PIWIK_VISITOR_ID, visitorId))).when(requestContext).getCookies();
        when(requestContext.getRequestUri()).thenReturn(URI.create("http://localhost"));

        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }

    @After
    public void tearDown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldCallGenerateUrlAndSendToPiwikAsynchronouslyWhenReportingCustomVariable() throws Exception {
        AnalyticsReporter analyticsReporter = spy(new AnalyticsReporter(piwikClient, new AnalyticsConfigurationBuilder().build()));
        CustomVariable customVariable = new CustomVariable(2, "IDP", "Experian");

        analyticsReporter.reportCustomVariable("friendly description of URL", requestContext, customVariable);

        URI expected = analyticsReporter.generateURI("friendly description of URL", requestContext, Optional.of(customVariable), Optional.of(visitorId));
        verify(piwikClient).report(expected, requestContext);
    }

    @Test
    public void shouldCallGenerateUrlAndSendToPiwkAsynchronously() throws MalformedURLException, URISyntaxException {
        String friendlyDescription = "friendly description of URL";
        URI piwikUri = URI.create("piwik");

        AnalyticsReporter analyticsReporter = spy(new AnalyticsReporter(piwikClient, new AnalyticsConfigurationBuilder().build()));

        doReturn(piwikUri).when(analyticsReporter).generateURI(friendlyDescription, requestContext, Optional.<CustomVariable>absent(), Optional.of(visitorId));

        analyticsReporter.report(friendlyDescription, requestContext);

        verify(piwikClient).report(piwikUri, requestContext);
    }

    @Test
    public void shouldHandleAnyExceptions() throws MalformedURLException, URISyntaxException {

        String friendlyDescription = "friendly description of URL";

        AnalyticsReporter analyticsReporter = spy(new AnalyticsReporter(piwikClient, new AnalyticsConfigurationBuilder().build()));

        doThrow(new RuntimeException("error")).when(analyticsReporter).generateURI(friendlyDescription, requestContext, Optional.<CustomVariable>absent(), Optional.of(visitorId));

        analyticsReporter.report(friendlyDescription, requestContext);
    }

    @Test
    public void shouldGeneratePiwikUrl() throws MalformedURLException, URISyntaxException {
        DateTime now = DateTime.now();

        when(requestContext.getHeaderString("Referer")).thenReturn("http://piwikserver/referrerUrl");
        when(requestContext.getRequestUri()).thenReturn(new URI("http://piwikserver/requestUrl"));

        AnalyticsConfiguration analyticsConfiguration = new AnalyticsConfigurationBuilder().build();
        URIBuilder expectedURI = new URIBuilder(format("http://piwik-digds.rhcloud.com/analytics?idsite={0}&rec=1&apiv=1&url=http%3A%2F%2Fpiwikserver%2FrequestUrl&urlref=http%3A%2F%2Fpiwikserver%2FreferrerUrl&_id=abc&ref=http%3A%2F%2Fpiwikserver%2FreferrerUrl&cookie=false&action_name=SERVER+friendly+description+of+URL", analyticsConfiguration.getSiteId()));

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        expectedURI.addParameter("cdt", fmt.print(now));

        AnalyticsReporter analyticsReporter = new AnalyticsReporter(piwikClient, analyticsConfiguration);

        URIBuilder testURI = new URIBuilder(analyticsReporter.generateURI("SERVER friendly description of URL", requestContext, Optional.<CustomVariable>absent(), Optional.of("abc")));

        Map<String, NameValuePair> expectedParams = Maps.uniqueIndex(expectedURI.getQueryParams(), new Function<NameValuePair, String>() {
            public String apply(NameValuePair from) {
                return from.getName();
            }
        });

        for (NameValuePair param : testURI.getQueryParams()) {
            assertThat(expectedParams).containsEntry(param.getName(), param);
        }

        assertThat(testURI.getQueryParams().size()).isEqualTo(expectedParams.size());
    }

    @Test
    public void shouldGeneratePiwikCustomVariableUrl() throws URISyntaxException {
        DateTime now = DateTime.now();
        String customVariable = "{\"1\":[\"RP\",\"HMRC BLA\"]}";

        AnalyticsConfiguration analyticsConfiguration = new AnalyticsConfigurationBuilder().build();
        URIBuilder expectedURI = new URIBuilder(format("http://piwiki-dgds.rhcloud.com/analytics?_id=123&idsite={0}&rec=1&apiv=1&action_name=page-title&cookie=false", analyticsConfiguration.getSiteId()));
        expectedURI.addParameter("_cvar", customVariable);
        expectedURI.addParameter("url", requestContext.getRequestUri().toString());
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        expectedURI.addParameter("cdt", fmt.print(now));
        AnalyticsReporter analyticsReporter = new AnalyticsReporter(piwikClient, analyticsConfiguration);
        Optional<Cookie> piwikCookie = fromNullable(requestContext.getCookies().get(PIWIK_VISITOR_ID));
        Optional<String> visitorId = Optional.of(piwikCookie.get().getValue());
        Optional<CustomVariable> customVariableOptional = Optional.of(new CustomVariable(1, "RP", "HMRC BLA"));
        URIBuilder testURI = new URIBuilder(analyticsReporter.generateURI("page-title", requestContext, customVariableOptional, visitorId));

        Map<String, NameValuePair> expectedParams = Maps.uniqueIndex(expectedURI.getQueryParams(), new Function<NameValuePair, String>() {
            public String apply(NameValuePair from) {
                return from.getName();
            }
        });

        for (NameValuePair param : testURI.getQueryParams()) {
            assertThat(expectedParams).containsEntry(param.getName(), param);
        }

        assertThat(testURI.getQueryParams().size()).isEqualTo(expectedParams.size());
    }

    @Test
    public void simulatePageView_generatesExpectedParameters() {
        AnalyticsConfiguration config = new AnalyticsConfigurationBuilder().build();
        AnalyticsReporter reporter = new AnalyticsReporter(piwikClient, config);
        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);

        reporter.reportPageView("Title", requestContext, "http://page-view");

        Mockito.verify(piwikClient).report(captor.capture(), eq(requestContext));
        URIBuilder uriBuilder = new URIBuilder(captor.getValue());
        checkURIBase(uriBuilder.toString(), config.getPiwikServerSideUrl());
        checkURIURL(uriBuilder, "http://page-view");
        checkURITitle(uriBuilder, "Title");
        checkParams(uriBuilder, config.getSiteId().toString());
    }

    @Test
    public void simulatePageView_includesVisitorIdIfPresent() {
        AnalyticsReporter reporter = new AnalyticsReporter(piwikClient, new AnalyticsConfigurationBuilder().build());
        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);

        reporter.reportPageView("Title", requestContext, "http://page-view");

        Mockito.verify(piwikClient).report(captor.capture(), eq(requestContext));
        checkVisitorId(captor.getValue().getQuery(), visitorId);
    }

    @Test
    public void simulatePageView_handlesMissingVisitorId() {
        when(requestContext.getCookies()).thenReturn(ImmutableMap.of());
        AnalyticsConfiguration config = new AnalyticsConfigurationBuilder().build();
        AnalyticsReporter reporter = new AnalyticsReporter(piwikClient, config);
        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);

        reporter.reportPageView("Title", requestContext, "http://page-view");

        Mockito.verify(piwikClient).report(captor.capture(), eq(requestContext));
        String query = captor.getValue().getQuery();
        checkQueryParamMissing(query, "_id");
    }

    @Test
    public void simulatePageView_doesNotReportIfAnalyticsIsDisabled() {
        AnalyticsConfiguration config = new AnalyticsConfigurationBuilder().setEnabled(false).build();
        AnalyticsReporter reporter = new AnalyticsReporter(piwikClient, config);

        reporter.reportPageView("Title", requestContext, "http://page-view");

        Mockito.verify(piwikClient, never()).report(any(URI.class), any(ContainerRequest.class));
    }

    private void checkCommonParams(URIBuilder uriBuilder, String siteId) {
        checkQueryParam(uriBuilder, "idsite", siteId);
        checkQueryParam(uriBuilder, "apiv", "1");
        checkQueryParam(uriBuilder, "rec", "1");
    }

    private void checkVisitorId(String query, String visitorId) {
        checkQueryParam(query, "_id", visitorId);
    }

    private void checkURIURL(URIBuilder query, String expected) {
        checkQueryParam(query, "url", expected);
    }

    private void checkURIBase(String uri, String expected) {
        assertThat(uri.indexOf(expected)).isEqualTo(0);
    }

    private void checkURITitle(URIBuilder query, String title) {
        checkQueryParam(query, "action_name", title);
    }

    private void checkQueryParam(String query, String name, String expected) {
        assertThat(queryContains(query, name, expected)).as("Looking for param %s with value %s in query %s", name,
                expected, query).isTrue();
    }

    private void checkQueryParam(URIBuilder uriBuilder, String name, String expected) {
        assertThat(uriBuilder.getQueryParams().contains(new BasicNameValuePair(name, expected))).as("Looking for param %s with value %s", name, expected).isTrue();
    }

    private boolean queryContains(String query, String name, String expectedValue) {
        String match = name + "=" + expectedValue;
        return ImmutableList.copyOf(query.split("&")).stream().anyMatch(match::equals);
    }

    private void checkQueryParamMissing(String query, String name) {
        assertThat(query.contains(name + "=")).isFalse();
    }

    private void checkParams(URIBuilder uriBuilder, String siteId) {
        checkCommonParams(uriBuilder, siteId);
        checkQueryParam(uriBuilder, "cookie", "false");
        checkQueryParam(uriBuilder, "cdt", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(DateTime.now()));
    }

}

package uk.gov.ida.filters;

import org.junit.Before;
import org.junit.Test;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AcceptLanguageFilterTest{

    private HttpServletRequest request = mock(HttpServletRequest.class);
    private String encodingHeader = "someheader";
    @Before
    public void setUp(){
        ArrayList<String> headers = new ArrayList<>();
        headers.add(HttpHeaders.ACCEPT_LANGUAGE);
        headers.add(HttpHeaders.ACCEPT_ENCODING);
        when(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE)).thenReturn("en-US,en;q=0.8,es-419;q=0.6,es;q=0.4");
        when(request.getHeader(HttpHeaders.ACCEPT_ENCODING)).thenReturn(encodingHeader);
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(headers));

    }
    @Test
    public void getHeaderNames_removesAcceptLanguageHeader(){
        AcceptLanguageFilter f = new AcceptLanguageFilter();
        Enumeration<String> headerNames = f.new HideAcceptLanguage(request).getHeaderNames();
        verify(request).getHeaderNames();
        assertThat(headerNames.nextElement()).contains(HttpHeaders.ACCEPT_ENCODING);
    }

    @Test
    public void getHeader_returnsValueOfAcceptLanguageHeaderNullForOthers(){
        AcceptLanguageFilter f = new AcceptLanguageFilter();
        assertThat(f.new HideAcceptLanguage(request).getHeader(HttpHeaders.ACCEPT_ENCODING)).isEqualTo(encodingHeader);
        assertNull(f.new HideAcceptLanguage(request).getHeader(HttpHeaders.ACCEPT_LANGUAGE));
    }
}

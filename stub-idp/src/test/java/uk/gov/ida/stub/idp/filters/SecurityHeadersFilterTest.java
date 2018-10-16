package uk.gov.ida.stub.idp.filters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SecurityHeadersFilterTest {

    @Mock
    private ContainerRequestContext containerRequestContext;

    @Mock
    private ContainerResponseContext containerResponseContext;

    @Test
    public void checkSecurityHeadersAreAdded() throws IOException {
        SecurityHeadersFilter securityHeadersFilter = new SecurityHeadersFilter();
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(containerResponseContext.getHeaders()).thenReturn(headers);

        securityHeadersFilter.filter(containerRequestContext, containerResponseContext);

        assertThat(headers.keySet().size()).isEqualTo(4);
        checkSecurityHeaders(headers);
    }

    public static void checkSecurityHeaders(MultivaluedMap<String, Object> headers) {
        assertThat(headers.containsKey("X-Frame-Options")).isTrue();
        assertThat(headers.get("X-Frame-Options").size()).isEqualTo(1);
        assertThat(headers.get("X-Frame-Options").get(0)).isEqualTo("DENY");
        assertThat(headers.containsKey("X-XSS-Protection")).isTrue();
        assertThat(headers.get("X-XSS-Protection").size()).isEqualTo(1);
        assertThat(headers.get("X-XSS-Protection").get(0)).isEqualTo("1; mode=block");
        assertThat(headers.containsKey("X-Content-Type-Options")).isTrue();
        assertThat(headers.get("X-Content-Type-Options").get(0)).isEqualTo("nosniff");
        assertThat(headers.get("X-Content-Type-Options").size()).isEqualTo(1);
        assertThat(headers.containsKey("Content-Security-Policy")).isTrue();
        assertThat(headers.get("Content-Security-Policy").size()).isEqualTo(1);
        assertThat(headers.get("Content-Security-Policy").get(0)).isEqualTo("default-src 'self'; font-src data:; img-src 'self'; object-src 'none'; style-src 'self' 'unsafe-inline'; script-src 'self';");
    }

}

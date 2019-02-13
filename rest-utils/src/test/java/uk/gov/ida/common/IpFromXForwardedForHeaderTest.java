package uk.gov.ida.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IpFromXForwardedForHeaderTest {
    @Mock
    HttpServletRequest httpServletRequest;

    @Test
    public void getPrincipalIpAddress_shouldReturnXForwardFromWhenExists(){
        String expectedPrincipalIp = "PrincipalIp";
        when(httpServletRequest.getHeader(HttpHeaders.X_FORWARDED_FOR)).thenReturn(expectedPrincipalIp);

        IpFromXForwardedForHeader ipFromXForwardedForHeader = new IpFromXForwardedForHeader();

        String actualPrincipalIpAddress = ipFromXForwardedForHeader.getPrincipalIpAddress(httpServletRequest);
        assertThat(actualPrincipalIpAddress).isEqualTo(expectedPrincipalIp);
    }

    @Test
    public void getPrincipalIpAddress_shouldReturnNotPresentWhenHeaderMissing(){
        IpFromXForwardedForHeader ipFromXForwardedForHeader = new IpFromXForwardedForHeader();

        String actualPrincipalIpAddress = ipFromXForwardedForHeader.getPrincipalIpAddress(httpServletRequest);

        assertThat(actualPrincipalIpAddress).isEqualTo(IpFromXForwardedForHeader.IP_ADDRESS_NOT_PRESENT_VALUE);
    }
}

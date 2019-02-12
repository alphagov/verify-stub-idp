package httpstub;

import com.google.common.collect.ImmutableMultimap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExpectedRequestTest {

    @Mock
    private RecordedRequest receivedRequest;

    @Before
    public void setupMocks() {
        when(receivedRequest.getPath()).thenReturn("/some/path/to/foo");
        when(receivedRequest.getMethod()).thenReturn("GET");
        when(receivedRequest.getHeaders()).thenReturn(ImmutableMultimap.<String, String>of("Key1", "Value1", "Key1", "Value3", "Key2", "Value2"));
        when(receivedRequest.getEntity()).thenReturn("any-body");
    }

    @Test
    public void shouldApplyIfPathIsNullInExpectedRequest() throws Exception {
        assertThat(new ExpectedRequest(null, null, null, null).applies(receivedRequest)).isTrue();
        verify(receivedRequest, never()).getPath();
        verify(receivedRequest, never()).getMethod();
        verify(receivedRequest, never()).getHeaders();
        verify(receivedRequest, never()).getEntity();
    }

    @Test
    public void shouldApplyIfPathSetInExpectedRequestAndMatchesReceivedRequestPath() throws Exception {
        assertThat(new ExpectedRequest("/some/path/to/foo", null, null, null).applies(receivedRequest)).isTrue();
        verify(receivedRequest, times(1)).getPath();
    }

    @Test
    public void shouldNotApplyIfPathSetInExpectedRequestAndDoesntMatchesReceivedRequestPath() throws Exception {
        assertThat(new ExpectedRequest("/some/path/to/bar", null, null, null).applies(receivedRequest)).isFalse();
        verify(receivedRequest, times(1)).getPath();
    }

    @Test
    public void shouldApplyIfMethodSetInExpectedRequestAndMatchesReceivedRequestMethod() throws Exception {
        assertThat(new ExpectedRequest(null, "GET", null, null).applies(receivedRequest)).isTrue();
        verify(receivedRequest, times(1)).getMethod();
    }

    @Test
    public void shouldNotApplyIfMethodSetInExpectedRequestAndDoesntMatchesReceivedRequestMethod() throws Exception {
        assertThat(new ExpectedRequest(null, "POST", null, null).applies(receivedRequest)).isFalse();
        verify(receivedRequest, times(1)).getMethod();
    }

    @Test
    public void shouldApplyIfBodySetInExpectedRequestAndMatchesReceivedRequestEntity() throws Exception {
        assertThat(new ExpectedRequest(null, null, null, "any-body").applies(receivedRequest)).isTrue();
        verify(receivedRequest, times(1)).getEntity();
    }

    @Test
    public void shouldNotApplyIfBodySetInExpectedRequestAndDoesntMatchesReceivedRequestEntity() throws Exception {
        assertThat(new ExpectedRequest(null, null, null, "some-body").applies(receivedRequest)).isFalse();
        verify(receivedRequest, times(1)).getEntity();
    }

    @Test
    public void shouldApplyIfHeadersSetInExpectedRequestAreAllFoundInReceivedRequestHeaders() throws Exception {
        ImmutableMultimap<String, String> headers = ImmutableMultimap.of("Key1", "Value1", "Key2", "Value2", "Key1", "Value3");
        assertThat(new ExpectedRequest(null, null, headers, null).applies(receivedRequest)).isTrue();
        verify(receivedRequest, times(1)).getHeaders();
    }

    @Test
    public void shouldApplyIfHeadersSetInExpectedRequestAreEmpty() throws Exception {
        ImmutableMultimap<String, String> headers = ImmutableMultimap.of();
        assertThat(new ExpectedRequest(null, null, headers, null).applies(receivedRequest)).isTrue();
        verify(receivedRequest, times(1)).getHeaders();
    }

    @Test
    public void shouldNotApplyIfHeadersSetInExpectedRequestAreNotAllFoundInReceivedRequestHeaders() throws Exception {
        ImmutableMultimap<String, String> headers = ImmutableMultimap.of("Key1", "Value1", "Key2", "Value2", "Key1", "Value3", "Key3", "Value4");
        assertThat(new ExpectedRequest(null, null, headers, null).applies(receivedRequest)).isFalse();
        verify(receivedRequest, times(1)).getHeaders();
    }
}

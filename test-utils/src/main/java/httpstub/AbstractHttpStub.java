package httpstub;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.getLast;

public abstract class AbstractHttpStub {
    protected static final int RANDOM_PORT = 0;
    protected Server server;
    protected List<RecordedRequest> recordedRequests = new CopyOnWriteArrayList<>();
    protected List<RequestAndResponse> requestsAndResponses = new CopyOnWriteArrayList<>();

    public AbstractHttpStub(int port) {
        server = new Server(port);
        server.setHandler(createHandler());
    }

    public final void start() {
        try {
            server.start();
        } catch (Exception e) {
            throw propagate(e);
        }
    }

    public final void stop() throws Exception {
        server.setStopTimeout(0);
        server.stop();
    }

    public final void reset() {
        recordedRequests.clear();
        requestsAndResponses.clear();
    }

    public final int getHttpPort() {
        return ((ServerConnector) server.getConnectors()[0]).getLocalPort();
    }

    public final void register(RequestAndResponse requestAndResponse) {
        requestsAndResponses.add(requestAndResponse);
    }

    public final int getCountOfRequestsTo(final String path) {
        return Iterables.size(Iterables.filter(recordedRequests, new Predicate<RecordedRequest>() {
            @Override
            public boolean apply(RecordedRequest input) {
                return input.getPath().equals(path);
            }
        }));
    }

    public final List<RecordedRequest> getRecordedRequests() { return recordedRequests; }

    public final RecordedRequest getLastRequest() {
        return getLast(recordedRequests);
    }

    public final int getCountOfRequests() {
        return recordedRequests.size();
    }

    public abstract StubHandler createHandler();
}

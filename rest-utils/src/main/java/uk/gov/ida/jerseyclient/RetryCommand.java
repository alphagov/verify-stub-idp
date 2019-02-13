package uk.gov.ida.jerseyclient;

import com.codahale.metrics.Meter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import java.util.function.Supplier;

import static java.lang.String.format;

public class RetryCommand<T> {

    private static final Logger LOG = LoggerFactory.getLogger(RetryCommand.class);

    private int retryCounter;
    private int maxRetries;
    private Class exceptionClass;
    private Meter retryMeter;

    public RetryCommand(int maxRetries) {
        this(maxRetries, Exception.class, null);
    }

    public RetryCommand(int maxRetries, Class exceptionClass) {
        this(maxRetries, exceptionClass, null);
    }

    public RetryCommand(int maxRetries, Meter retryMeter) {
        this(maxRetries, Exception.class, retryMeter);
    }

    public RetryCommand(int maxRetries, Class exceptionClass, Meter retryMeter) {
        this.exceptionClass = exceptionClass;
        this.retryMeter = retryMeter;
        this.retryCounter = 0;
        this.maxRetries = maxRetries;
    }

    public T execute(Supplier<T> function) {
        try {
            return function.get();
        } catch (Exception e) {
            if(!exceptionClass.isInstance(e)) throw e;
            if(retryCounter >= maxRetries) return failAndStopRetry(e, function);

            if(retryCounter == 0) { logInitialFail(e, function); }
            else { logRetryFail(e, function); }

            retryCounter++;
            if(retryMeter != null) retryMeter.mark();

            return execute(function);
        }
    }

    private void logRetryFail(Exception e, Supplier<T> function) {
        LOG.warn(format("Command %s failed on retry %d of %d.",
                function.toString(),
                retryCounter,
                maxRetries), e);
    }

    private void logInitialFail(Exception e, Supplier<T> function) {
        LOG.warn(format("Command %s failed, will be retried %d times.",
                function.toString(),
                maxRetries), e);
    }

    private T failAndStopRetry(Exception e, Supplier<T> function) {
        LOG.warn("Max retries exceeded for " + function.toString());
        throw new ProcessingException(format("Command %s failed on all of %d retries.",
                function.toString(),
                maxRetries), e);
    }
}

package uk.gov.ida.shared.utils.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.util.UUID;

public class LevelLogger<T> {

    private final Logger log;

    private LevelLogger(Class<T> clazz) {
        log = LoggerFactory.getLogger(clazz);
    }

    static <T> LevelLogger<T> getLevelLogger(Class<T> clazz) {
        return new LevelLogger<T>(clazz);
    }

    public void log(Level level, Exception exception, UUID errorId){

        if (level == Level.ERROR) {
            log.error(LogFormatter.formatLog(errorId, exception.getMessage()),exception);
        }

        if (level == Level.WARN) {
            log.warn(LogFormatter.formatLog(errorId, exception.getMessage()), exception);
        }

        if (level == Level.INFO) {
            log.info(LogFormatter.formatLog(errorId, exception.getMessage()),exception);
        }

        if (level == Level.DEBUG) {
            log.debug(LogFormatter.formatLog(errorId, exception.getMessage()), exception);
        }
    }

    public void log(Level level, Exception exception) {
        log(level, exception, UUID.randomUUID());
    }
}

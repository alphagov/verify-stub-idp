package uk.gov.ida.shared.utils.logging;

public class LevelLoggerFactory<T> {
    public LevelLogger createLevelLogger(Class<T> clazz) {
        return LevelLogger.getLevelLogger(clazz);
    }
}

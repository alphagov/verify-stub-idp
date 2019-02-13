package uk.gov.ida.shared.utils.optionals;

import java.util.Optional;

public class OptionalHelper {

    private OptionalHelper() {}

    public static <T>Optional toJavaOptional(com.google.common.base.Optional<T> optional) {
        return Optional.ofNullable(optional.orNull());
    }

    public static <T>com.google.common.base.Optional toGuavaOptional(Optional<T> optional) {
        return com.google.common.base.Optional.fromNullable(optional.orElse(null));
    }

}

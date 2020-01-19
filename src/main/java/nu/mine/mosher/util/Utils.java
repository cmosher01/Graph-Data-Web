package nu.mine.mosher.util;

import java.util.Objects;

public final class Utils {
    private Utils() {}

    public static String str(final Object object) {
        return Objects.isNull(object) ? "" : object.toString();
    }
}

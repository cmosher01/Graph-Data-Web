package nu.mine.mosher.util;


import org.apache.wicket.model.PropertyModel;

import java.util.*;

public final class Utils {
    public static UUID uuid(final Object entity) {
        return (UUID)new PropertyModel<>(entity, "uuid").getObject();
    }

//    public static String ids(final Collection r) {
//        final StringBuilder sb = new StringBuilder();
//        boolean first = true;
//        for (final Object o : r) {
//            if (first) {
//                first = false;
//            } else {
//                sb.append(',');
//            }
//            sb.append(id(o).toString());
//        }
//        return sb.toString();
//    }

    public static String str(final Object object) {
        return Objects.isNull(object) ? "" : object.toString();
    }

    private Utils() {}
}

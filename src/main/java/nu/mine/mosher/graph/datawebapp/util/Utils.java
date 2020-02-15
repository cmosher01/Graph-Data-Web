package nu.mine.mosher.graph.datawebapp.util;


import org.apache.wicket.model.PropertyModel;
import org.neo4j.ogm.session.event.*;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.time.*;
import java.util.*;

public final class Utils {
    public static class UtcModifiedUpdater extends EventListenerAdapter {
        @Override
        public void onPreSave(Event event) {
            super.onPreSave(event);
            new PropertyModel<>(event.getObject(), "utcModified").setObject(ZonedDateTime.now(ZoneOffset.UTC));
        }
    }

    @SuppressWarnings("rawtypes")
    public static Serializable create(final Class cls) {
        try {
            return initEntity(instance(cls));
        } catch (final Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    private static Object instance(final Class cls) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return
            Arrays.
            stream(cls.getDeclaredConstructors()).
            filter(c -> c.getGenericParameterTypes().length == 0).
            findAny().
            orElseThrow().
            newInstance();
    }

    private Utils() {}

    public static String str(final Object object) {
        return Objects.isNull(object) ? "" : object.toString();
    }

    public static Serializable resetEntity(final Serializable entity) {
        new PropertyModel<>(entity, "id").setObject(null);
        new PropertyModel<>(entity, "version").setObject(null);
        return entity;
    }

    public static UUID uuid(final Object entity) {
        return (UUID)Objects.requireNonNull(new PropertyModel<>(entity, "uuid").getObject());
    }

    public static Serializable initEntity(final Object entity) {
        new PropertyModel<>(entity, "uuid").setObject(UUID.randomUUID());
        new PropertyModel<>(entity, "utcCreated").setObject(ZonedDateTime.now(ZoneOffset.UTC));
        return (Serializable)entity;
    }
}

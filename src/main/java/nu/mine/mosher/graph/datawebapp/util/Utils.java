package nu.mine.mosher.graph.datawebapp.util;


import nu.mine.mosher.graph.datawebapp.GraphDataWebApp;
import nu.mine.mosher.graph.datawebapp.store.Store;
import org.apache.wicket.Application;
import org.apache.wicket.model.PropertyModel;
import org.neo4j.ogm.exception.OptimisticLockingException;
import org.neo4j.ogm.metadata.MetaData;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.time.*;
import java.util.*;

public final class Utils {
    @SuppressWarnings("rawtypes")
    public static Serializable create(final Class cls) {
        try {
            return initEntity(instance(cls));
        } catch (final Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    public static MetaData metaData() {
        return store().getSession().metadata();
    }

    public static String str(final Object object) {
        return Objects.isNull(object) ? "" : object.toString();
    }

    public static Serializable resetEntity(final Serializable entity) {
        new PropertyModel<>(entity, "id").setObject(null);
        new PropertyModel<>(entity, "version").setObject(null);
        return entity;
    }

    public static UUID uuid(final Object entity) {
        return prop(entity, "uuid", UUID.class);
    }

    public static Long id(final Object entity) {
        return prop(entity, "id", Long.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> T prop(final Object object, final String nameProperty, final Class<T> cls) {
        return
            (T)
            new PropertyModel<>(Objects.requireNonNull(object), Objects.requireNonNull(nameProperty)).
            getObject();
    }

    public static Serializable same(final UUID expected, final Object entity) {
        final UUID actual = Utils.uuid(Objects.requireNonNull(entity));
        if (!actual.equals(Objects.requireNonNull(expected))) {
            throw new OptimisticLockingException("Retrieved unexpected object from database: expected="+expected+", actual="+actual);
        }
        return (Serializable)entity;
    }


    public static Store store() {
        return app().store();
    }

    public static Props props() {
        return app().props();
    }

    public static org.neo4j.ogm.session.Session ogm() {
        return store().getSession().session();
    }

    public static GraphDataWebApp app() {
        return (GraphDataWebApp)Application.get();
    }



    @SuppressWarnings("rawtypes")
    private static Serializable instance(final Class cls) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return
            (Serializable)
            Arrays.
            stream(cls.getDeclaredConstructors()).
            filter(c -> c.getGenericParameterTypes().length == 0).
            findAny().
            orElseThrow().
            newInstance();
    }

    private static Serializable initEntity(final Serializable entity) {
        new PropertyModel<>(entity, "uuid").setObject(UUID.randomUUID());
        new PropertyModel<>(entity, "utcCreated").setObject(ZonedDateTime.now(ZoneOffset.UTC));
        return entity;
    }

    private Utils() {}
}

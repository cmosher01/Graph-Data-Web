package nu.mine.mosher.graph.datawebapp.util;

import nu.mine.mosher.graph.datawebapp.store.Store;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

/**
 * Tools for using reflection to get information on fields ("properties" or "props")
 * of a class.
 *
 * The only properties we handle are:
 * 1. primitive (non-entity objects) scalar values
 * 2. singular references (to entity objects)
 * 3. multiple references (Collection<Entity>)
 *
 * Ignore: arrays, Collections of non-entities, static fields, Optional fields
 *
 */
@SuppressWarnings("rawtypes")
public final class Props {
    private final Store store;

    public Props(final Store store) {
        this.store = store;
    }

    public static class Prop implements Serializable {
        public final String name;
        public final boolean readOnly;
        public final boolean isBoolean;

        public Prop(final String name, final boolean readOnly, final boolean isBoolean) {
            this.name = name;
            this.readOnly = readOnly;
            this.isBoolean = isBoolean;
        }
    }

    public static class Ref implements Serializable {
        public final String name;
        public final Class cls;
        public final boolean collection;

        private Ref(final String name, final Class cls, final boolean collection) {
            this.name = name;
            this.cls = cls;
            this.collection = collection;
        }
    }

    // 3. multiple references (Collection<Entity>)
    public List<Ref> refsMultiple(final Class cls) {
        return
            collections(cls).
            filter(f -> isEntity(getGenType(f))).
            map(f -> new Ref(f.getName(), getGenType(f), true)).
            collect(Collectors.toList());
    }

    // 2. singular references (to entity objects)
    public List<Ref> refsSingular(final Class cls) {
        return
            scalars(cls).
            filter(f -> isEntity(f.getType())).
            map(f -> new Ref(f.getName(), f.getType(), false)).
            collect(Collectors.toList());
    }

    // 1. primitive (non-entity objects) scalar values
    // returns a list of the names of those properties
    public List<Prop> properties(final Class cls) {
        return
            scalars(cls).
            filter(f -> !isEntity(f.getType())).
            map(f -> new Prop(f.getName(), f.isAnnotationPresent(ReadOnly.class), isBoolean(f))).
            collect(Collectors.toList());
    }

    private static boolean isBoolean(Field f) {
        return f.getType().equals(boolean.class) || Boolean.class.isAssignableFrom(f.getType());
    }

    private boolean isEntity(final Class cls) {
        return this.store.isEntity(cls);
    }

    private static Stream<Field> scalars(final Class cls) {
        return
            props(cls).
            filter(f -> !f.getType().isArray()).
            filter(f -> !Collection.class.isAssignableFrom(f.getType())).
            filter(f -> !Optional.class.isAssignableFrom(f.getType()));
    }

    private static Stream<Field> collections(final Class cls) {
        return
            props(cls).
            filter(f -> Collection.class.isAssignableFrom(f.getType()));
    }

    private static Stream<Field> props(final Class cls) {
        return
            Arrays.
            stream(cls.getFields()).
            filter(f -> !f.isSynthetic()).
            filter(f -> !Modifier.isStatic(f.getModifiers()));
    }

    private static Class getGenType(final Field field) {
        final ParameterizedType stringListType = (ParameterizedType)field.getGenericType();
        return (Class)stringListType.getActualTypeArguments()[0];
    }
}

package nu.mine.mosher.model;

import java.util.*;

public class Models {
    private static final Set<Class> entities = Set.of(Event.class, Persona.class, Role.class);

    public static Set<Class> entities() {
        return entities;
    }

    public static boolean isEntity(final Class cls) {
        return entities.stream().anyMatch(c -> c.isAssignableFrom(cls));
    }
}

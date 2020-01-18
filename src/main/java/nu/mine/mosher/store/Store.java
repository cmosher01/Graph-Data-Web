package nu.mine.mosher.store;

import nu.mine.mosher.model.Models;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.*;

public class Store {
    public Store(final Set<Class> entities) {
        // TODO check to ensure all are Serializable, etc.
        entities.forEach(e -> store.put(e, new StoreEntity(e)));
    }




    public int count(final Class cls) {
        return store(cls).getSize();
    }

    public List getAll(final Class cls) {
        return store(cls).getAll();
    }

    public Serializable load(final Class cls, final long id) {
        if (id == 0L && Models.isEntity(cls)) {
            return create(cls);
        }
        return store(cls).get(id);
    }

    private Serializable create(final Class cls) {
        try {
            return (Serializable)Arrays.
                stream(cls.getDeclaredConstructors()).
                filter(c -> c.getGenericParameterTypes().length == 0).
                findAny().
                orElseThrow().
                newInstance();
        } catch (final Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    public void save(final Serializable entity) {
        store(entity.getClass()).save(entity);
    }

    public void delete(final Class cls, final long id) {
        store(cls).delete(id);
    }












    private StoreEntity store(final Class cls) {
        return Optional.ofNullable(this.store.get(cls)).orElseThrow();
    }

    private final Map<Class, StoreEntity> store = new HashMap<>();

    private static class StoreEntity {
        private final Class type;
        private long id;
        private final Map<Long, Serializable> store = new HashMap<>();

        public StoreEntity(final Class type) {
            this.type = type;
        }

        public int getSize() {
            return this.store.size();
        }

        public ArrayList getAll() {
            return new ArrayList(this.store.values());
        }

        public Serializable get(final long id) {
            return Optional.ofNullable(this.store.get(id)).orElseThrow();
        }

        public synchronized long createId() {
            return ++this.id;
        }

        public void delete(final long id) {
            if (id != 0L) {
                this.store.remove(id);
            }
        }

        public void save(final Serializable entity) {
            final PropertyModel<Long> propID = PropertyModel.of(entity, "id");
            Long id = propID.getObject();
            if (Objects.isNull(id) || id == 0L) {
                id =createId();
                propID.setObject(id);
            }
            this.store.put(id, entity);
        }

//    public void dump() {
//        this.storePersona.values().stream().map(p -> ""+p.getId()+": "+p.getDescription()).forEach(System.out::println);
//    }
    }
}

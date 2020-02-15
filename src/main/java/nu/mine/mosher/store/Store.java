package nu.mine.mosher.store;

import org.apache.wicket.model.PropertyModel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.metadata.ClassInfo;
import org.neo4j.ogm.session.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Store {
    private static final Map<String, Session> cacheSession = new ConcurrentHashMap<>();

    private final Logger LOG = LoggerFactory.getLogger(Store.class);

    private final SessionFactory factorySession;

    public Store(final String... packages) {
        final GraphDatabaseService db = new GraphDatabaseFactory().
            newEmbeddedDatabaseBuilder(new File("database")).
            newGraphDatabase();

        final Configuration configuration = new Configuration.Builder().build();
        final EmbeddedDriver driver = new EmbeddedDriver(db, configuration);
        this.factorySession = new SessionFactory(driver, packages);
    }

    public boolean isEntity(final Class cls) {
        return Objects.nonNull(this.factorySession.metaData().classInfo(cls));
    }

    public List<Class> entities() {
        return
            this.
            factorySession.
            metaData().
            persistentEntities().
            stream().
            map(ClassInfo::getUnderlyingClass).
            collect(Collectors.toUnmodifiableList());
    }

    public long count(final Class cls) {
        final Session session = this.factorySession.openSession();
        return session.countEntitiesOfType(cls);
    }

    public Collection getAll(final Class cls) {
        final Session session = this.factorySession.openSession();
        final Collection entities = Objects.requireNonNull(session.loadAll(cls, 1));
        entities.forEach(e -> LOG.info("Loaded {}", e));
        return entities;
    }

    public Session getSession(final String id) {
        return cacheSession.computeIfAbsent(id, k -> this.factorySession.openSession());
    }

    public void dropSession(final String id) {
        cacheSession.remove(id);
    }

    public static Serializable create(final Class cls) {
        try {
            return uuidstamp(Arrays.
                stream(cls.getDeclaredConstructors()).
                filter(c -> c.getGenericParameterTypes().length == 0).
                findAny().
                orElseThrow().
                newInstance());
        } catch (final Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    private static Serializable uuidstamp(final Object entity) {
        new PropertyModel<>(entity, "uuid").setObject(UUID.randomUUID());
        return (Serializable)entity;
    }
}

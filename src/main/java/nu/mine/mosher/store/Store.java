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
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Store {
    private final Logger LOG = LoggerFactory.getLogger(Store.class);

    private final SessionFactory factorySession;

    public Store(final String... packages) {
// Neo4j 4.0:
//        final DatabaseManagementService dbms;
//        try {
//            dbms = new DatabaseManagementServiceBuilder(new File("./run/data").getCanonicalFile()).build();
//        } catch (IOException e) {
//            throw new IllegalArgumentException(e);
//        }
//        Runtime.getRuntime().addShutdownHook(new Thread(dbms::shutdown));
//        try {
//            dbms.createDatabase(DEFAULT_DATABASE_NAME);
//        } catch (final Throwable e) {
//            // OK
//        }
//        final GraphDatabaseService db = dbms.database(DEFAULT_DATABASE_NAME);

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

    public boolean isRelationshipEntity(final Class cls) {
        final ClassInfo info = this.factorySession.metaData().classInfo(cls);
        return Objects.nonNull(info) && info.isRelationshipEntity();
    }

    public boolean isNodeEntity(final Class cls) {
        final ClassInfo info = this.factorySession.metaData().classInfo(cls);
        return Objects.nonNull(info) && !info.isRelationshipEntity();
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

    private static final Map<String, Session> cacheSession = new HashMap<>();

    public Session getSession(final String id) {
        return cacheSession.computeIfAbsent(id, k -> this.factorySession.openSession());
    }

    public Session dropSession(final String id) {
        return cacheSession.remove(id);
    }

//    public Serializable load(final Class cls, final UUID uuid) {
//        final Session session = this.factorySession.openSession();
//        final Object entity = Objects.requireNonNull(session.load(cls, uuid));
//        return (Serializable)entity;
//    }
//
//    public boolean save(final Serializable entity) {
//        final Session session = this.factorySession.openSession();
//        try {
//            LOG.info("Saving {}", entity);
//            session.save(entity);
//            return true;
//        } catch (RuntimeException e) {
//            LOG.warn("Ignoring exception during save: {}", e.getMessage());
//            return false;
//        }
//    }
//
//    public void delete(final Serializable entity) {
//        final Session session = this.factorySession.openSession();
//        LOG.info("Deleting {}", entity);
//        session.delete(entity);
//    }



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

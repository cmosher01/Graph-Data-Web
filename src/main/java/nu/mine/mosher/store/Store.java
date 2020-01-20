package nu.mine.mosher.store;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.session.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Store {
    private final Logger LOG = LoggerFactory.getLogger(Store.class);

    private final SessionFactory factorySession;

    private final Map<String, Session> sessions = new HashMap<>();

    public Store(final Set<Class> entities) {
        // TODO check to ensure all are Serializable, etc.
//        entities.forEach(e -> verify(e));

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
        this.factorySession = new SessionFactory(driver, "nu.mine.mosher.app.sample.model");
    }

    private Session session(final String id) {
        this.sessions.computeIfAbsent(id, k -> this.factorySession.openSession());
        return this.sessions.get(id);
    }

    public long count(final Class cls, String sessionID) {
        final Session session = session(sessionID);
        return session.countEntitiesOfType(cls);
    }

    public Collection getAll(final Class cls, String sessionID) {
        final Session session = session(sessionID);
        final Collection entities = session.loadAll(cls, 1);
        entities.forEach(e -> LOG.info("Loaded {}", e));
        return entities;
    }

    public void save(final Serializable entity, String sessionID) {
        final Session session = session(sessionID);
        try {
            LOG.info("Saving {}", entity);
            session.save(entity, 1);
            LOG.info("Saved  {}", entity);
        } catch (RuntimeException e) {
            LOG.warn("Ignoring exception during save: {}", e.getMessage());
        }
    }

    public void delete(final Serializable entity, String sessionID) {
        final Session session = session(sessionID);
        LOG.info("Deleting {}", entity);
        session.delete(entity);
    }



    public static Serializable create(final Class cls) {
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
}

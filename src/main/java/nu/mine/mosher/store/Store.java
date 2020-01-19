package nu.mine.mosher.store;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.session.*;

import java.io.*;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Store {
    private final SessionFactory factorySession;

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



    public long count(final Class cls) {
        final Session session = this.factorySession.openSession();
        return session.countEntitiesOfType(cls);
    }

    public List getAll(final Class cls) {
        final Session session = this.factorySession.openSession();
        return List.copyOf(session.loadAll(cls, 1));
    }

    public Serializable load(final Class cls, final Long id) {
        if (id == 0L) {
            return create(cls);
        }
        final Session session = this.factorySession.openSession();
        return (Serializable)session.load(cls, id, 1);
    }

    public void save(final Serializable entity) {
        final Session session = this.factorySession.openSession();
        try {
            session.save(entity, 1);
        } catch (RuntimeException e) {
            System.out.println("This will happen when an edge has a null reference to a node");
            e.printStackTrace();
        }
    }

    public void delete(final Serializable entity) {
        final Session session = this.factorySession.openSession();
        session.delete(entity);
    }



    private static Serializable create(final Class cls) {
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

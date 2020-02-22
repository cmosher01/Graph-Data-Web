package nu.mine.mosher.graph.datawebapp.store;

import nu.mine.mosher.graph.datawebapp.util.Utils;
import nu.mine.mosher.graph.datawebapp.util.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.cypher.*;
import org.neo4j.ogm.cypher.query.Pagination;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.metadata.ClassInfo;
import org.neo4j.ogm.session.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public class Store {
    private final Map<String, Session> cacheSession = new ConcurrentHashMap<>();

    private final SessionFactory factorySession;

    public Store(final String bolt, final String username, final String password, final String... packages) {
        if (Objects.nonNull(bolt) && !bolt.isEmpty()) {
            Configuration configuration =
                new Configuration.Builder().
                useNativeTypes().
                strictQuerying().
                uri(bolt).
                credentials(username, password).
                build();
            this.factorySession = new SessionFactory(configuration, packages);
        } else {
            final GraphDatabaseService db =
                new GraphDatabaseFactory().
                newEmbeddedDatabaseBuilder(new File("database")).
                newGraphDatabase();
            final Configuration configuration =
                new Configuration.Builder().
                useNativeTypes().
                strictQuerying().
                build();
            final EmbeddedDriver driver = new EmbeddedDriver(db, configuration);
            this.factorySession = new SessionFactory(driver, packages);
        }
        this.factorySession.register(new Utils.UtcModifiedUpdater());
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
            filter(c -> !c.equals(GraphEntity.class)).
            collect(Collectors.toUnmodifiableList());
    }

    private static final List<Filter> OPTIMIZE_CYPHER_QUERY = Collections.emptyList();

    /**
     * Checks for existence of any entities of the given type in the database.
     * @param cls type of entity (node or relationship) to check
     * @return true if at least one node or relationship of type cls exists
     */
    public boolean any(final Class cls) {
        final Session session = this.factorySession.openSession();
        return session.count(cls, OPTIMIZE_CYPHER_QUERY) > 0;
    }

    public static final int PAGE_SIZE = 100;

    @SuppressWarnings("unchecked")
    public Collection getAll(final Class cls, final int page) {
        final Session session = this.factorySession.openSession();
        // TODO implement user query
        return Objects.requireNonNull(session.loadAll(cls, new Pagination(page, PAGE_SIZE), 1));
    }

    public Session getSession(final String id) {
        return cacheSession.computeIfAbsent(id, k -> this.factorySession.openSession());
    }

    public void dropSession(final String id) {
        cacheSession.remove(id);
    }
}

package nu.mine.mosher.graph.datawebapp.store;

import com.github.benmanes.caffeine.cache.*;
import nu.mine.mosher.graph.datawebapp.util.GraphEntity;
import org.apache.wicket.model.PropertyModel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.metadata.ClassInfo;
import org.neo4j.ogm.session.*;
import org.neo4j.ogm.session.event.*;

import java.io.File;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public class Store {
    private final LoadingCache<String, Session> cacheSession;
    private final SessionFactory factorySession;

    public Store(final String bolt, final String username, final String password, final String... packages) {
        this.factorySession = createSessionFactory(bolt, username, password, packages);
        this.factorySession.register(new EventListenerAdapter() {
            @Override
            public void onPreSave(Event event) {
                new PropertyModel<>(event.getObject(), "utcModified").setObject(ZonedDateTime.now(ZoneOffset.UTC));
            }
        });

        this.cacheSession = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(key -> this.factorySession.openSession());

        Scheduler.systemScheduler().schedule(
            ForkJoinPool.commonPool(),
            this.cacheSession::cleanUp,
            5, TimeUnit.MINUTES);
    }

    private static SessionFactory createSessionFactory(final String bolt, final String username, final String password, final String... packages) {
        final Configuration.Builder configBase = new Configuration.Builder().
            useNativeTypes().
            strictQuerying();

        final SessionFactory sf;
        if (Objects.nonNull(bolt) && !bolt.isEmpty()) {
            final Configuration config =
                configBase.
                uri(bolt).
                credentials(username, password).
                build();
            sf = new SessionFactory(config, packages);
        } else {
            final GraphDatabaseService db =
                new GraphDatabaseFactory().
                newEmbeddedDatabaseBuilder(new File("database")).
                newGraphDatabase();
            final EmbeddedDriver driver = new EmbeddedDriver(db, configBase.build());
            sf = new SessionFactory(driver, packages);
        }
        return sf;
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

    public List<Class> entityClasses() {
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

    public List<String> namesNodes() {
        return
            this.
            factorySession.
            metaData().
            persistentEntities().
            stream().
            filter(ci -> isNodeEntity(ci.getUnderlyingClass())).
            map(ClassInfo::neo4jName).
            collect(Collectors.toUnmodifiableList());
    }

    public List<String> namesRelationships() {
        return
            this.
            factorySession.
            metaData().
            persistentEntities().
            stream().
            filter(ci -> isRelationshipEntity(ci.getUnderlyingClass())).
            map(ClassInfo::neo4jName).
            collect(Collectors.toUnmodifiableList());
    }

//    private static final List<Filter> OPTIMIZE_CYPHER_QUERY = Collections.emptyList();
//
//    /**
//     * Checks for existence of any entities of the given type in the database.
//     * @param cls type of entity (node or relationship) to check
//     * @return true if at least one node or relationship of type cls exists
//     */
//    public boolean any(final Class cls) {
//        final Session session = this.factorySession.openSession();
//        return session.count(cls, OPTIMIZE_CYPHER_QUERY) > 0;
//    }

    public Session getSession(final String id) {
        return this.cacheSession.get(id);
    }

    public void dropSession(final String id) {
        this.cacheSession.invalidate(id);
    }
}

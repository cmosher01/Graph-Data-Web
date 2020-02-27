package nu.mine.mosher.graph.datawebapp.store;

import com.github.benmanes.caffeine.cache.*;
import nu.mine.mosher.graph.datawebapp.util.*;
import org.apache.wicket.Session;
import org.neo4j.ogm.metadata.ClassInfo;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public class Store implements AutoCloseable {
    private final Logger LOG = LoggerFactory.getLogger(Store.class);

    private final Cache<String, Neo4jConnection> connections;

    public Store() {
        this.connections =
            Caffeine.
            newBuilder().
            removalListener((String id, Neo4jConnection conn, RemovalCause cause) -> conn.close()).
            expireAfterWrite(1, TimeUnit.HOURS).
            build();

        Scheduler.systemScheduler().schedule(
            ForkJoinPool.commonPool(),
            this.connections::cleanUp,
            5, TimeUnit.MINUTES);
    }

    public boolean isEntity(final Class cls) {
        return Objects.nonNull(Utils.metaData().classInfo(cls));
    }

    public boolean isRelationshipEntity(final Class cls) {
        final ClassInfo info = Utils.metaData().classInfo(cls);
        return Objects.nonNull(info) && info.isRelationshipEntity();
    }

    public boolean isNodeEntity(final Class cls) {
        final ClassInfo info = Utils.metaData().classInfo(cls);
        return Objects.nonNull(info) && !info.isRelationshipEntity();
    }

    public List<Class> entityClasses() {
        return
            Utils.
            metaData().
            persistentEntities().
            stream().
            map(ClassInfo::getUnderlyingClass).
            filter(c -> !c.equals(GraphEntity.class)).
            collect(Collectors.toUnmodifiableList());
    }

    public List<String> namesNodes() {
        return
            Utils.
            metaData().
            persistentEntities().
            stream().
            filter(ci -> isNodeEntity(ci.getUnderlyingClass())).
            map(ClassInfo::neo4jName).
            collect(Collectors.toUnmodifiableList());
    }

    public List<String> namesRelationships() {
        return
            Utils.
            metaData().
            persistentEntities().
            stream().
            filter(ci -> isRelationshipEntity(ci.getUnderlyingClass())).
            map(ClassInfo::neo4jName).
            collect(Collectors.toUnmodifiableList());
    }

    public void createSession(final String bolt, final String username, final String password, final String... packages) {
        final String id = Session.get().getId();
        try {
            this.connections.put(id, new Neo4jConnection(bolt, username, password, packages));
        } catch (final RuntimeException cantConnect) {
            LOG.warn("Cannot connect", cantConnect);
        }
    }

    public Neo4jConnection getSession() {
        final String id = Session.get().getId();
        return Optional.ofNullable(this.connections.getIfPresent(id)).orElseThrow();
    }

    public boolean haveSession() {
        final String id = Session.get().getId();
        return Objects.nonNull(this.connections.getIfPresent(id));
    }

    public void dropSession() {
        final String id = Session.get().getId();
        this.connections.invalidate(id);
    }

    @Override
    public void close() {
        this.connections.invalidateAll();
        try {
            ForkJoinPool.commonPool().awaitTermination(7, TimeUnit.SECONDS);
        } catch (final InterruptedException propagate) {
            LOG.warn("interrupted", propagate);
            Thread.currentThread().interrupt();
        }
        this.connections.cleanUp();
    }
}

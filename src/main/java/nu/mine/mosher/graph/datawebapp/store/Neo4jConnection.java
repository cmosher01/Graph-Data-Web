package nu.mine.mosher.graph.datawebapp.store;

import org.apache.wicket.model.PropertyModel;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.metadata.MetaData;
import org.neo4j.ogm.session.*;
import org.neo4j.ogm.session.event.*;

import java.time.*;
import java.util.*;

import static nu.mine.mosher.graph.datawebapp.util.Utils.*;

public class Neo4jConnection implements AutoCloseable {
    private final SessionFactory factory;
    private final Session session;

    public Neo4jConnection(final String url, final String username, final String password, final String... packages) {
        final Configuration config =
            new
                Configuration.
                Builder().
                useNativeTypes().
                strictQuerying().
                uri(url).
                credentials(username, password).
                build();

        this.factory = new SessionFactory(config, packages);
        this.factory.register(new EventListenerAdapter() {
            @Override
            public void onPreSave(final Event event) {
                new PropertyModel<>(event.getObject(), "utcModified").setObject(ZonedDateTime.now(ZoneOffset.UTC));
            }
        });

        this.session = this.factory.openSession();

        this.session.query("CALL dbms.showCurrentUser()", Collections.emptyMap(), true);

        createFullTextIndex();
    }

    @Override
    public void close() {
        this.factory.close();
    }

    public SessionFactory factory() {
        return this.factory;
    }

    public Session session() {
        return this.session;
    }

    public MetaData metadata() {
        return this.factory.metaData();
    }


    private void createFullTextIndex() {
        final String csvIndexProperties = Optional.ofNullable(app().getInitParameter("index-properties")).orElse("name");
        final  List<String> idxProperties = Arrays.asList(csvIndexProperties.split(","));

        if (missing("fulltextNode")) {
            final String queryNod = "CALL db.index.fulltext.createNodeIndex(\"fulltextNode\", $entities, $properties)";
            this.session.query(void.class, queryNod, Map.of("entities", store().namesNodes(), "properties", idxProperties));
        }

        if (missing("fulltextRelationship")) {
            final String queryRel = "CALL db.index.fulltext.createRelationshipIndex(\"fulltextRelationship\", $entities, $properties)";
            this.session.query(void.class, queryRel, Map.of("entities", store().namesRelationships(), "properties", idxProperties));
        }
    }

    private boolean missing(final String name) {
        final String query = "CALL db.indexes() YIELD indexName WHERE indexName=$name RETURN COUNT(*)";
        final Iterable<Integer> result = this.session.query(int.class, query, Map.of("name", name));
        final int count = result.iterator().next();
        return count == 0;
    }
}

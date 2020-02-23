package nu.mine.mosher.graph.datawebapp;

import nu.mine.mosher.graph.datawebapp.store.Store;
import nu.mine.mosher.graph.datawebapp.util.*;
import nu.mine.mosher.graph.datawebapp.view.PageHome;
import nu.mine.mosher.graph.sample.imdb.Movie;
import org.apache.wicket.*;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

import java.time.ZonedDateTime;
import java.util.*;

public class GraphDataWebApp extends WebApplication {
    private List<String> idxProperties;
    private Store store;
    private Props props;
    private String title;
    private String stylesheet;

    @Override
    public Class<? extends WebPage> getHomePage() {
        return PageHome.class;
    }

    @Override
    protected void init() {
        super.init();

        String csvPackageNames = Optional.ofNullable(getInitParameter("packages")).orElse(Movie.class.getPackageName());
        this.store = new Store(
            getInitParameter("neo4j-bolt-url"),
            getInitParameter("neo4j-username"),
            getInitParameter("neo4j-password"),
            csvPackageNames.split(","));
        this.props = new Props(this.store);
        this.title = Optional.ofNullable(getInitParameter("title")).orElse(csvPackageNames);
        this.stylesheet = Optional.ofNullable(getInitParameter("stylesheet")).orElse("");
        createFullTextIndex();
    }

    public synchronized void createFullTextIndex() {
        if (this.idxProperties == null) {
            final org.neo4j.ogm.session.Session ogm = ((GraphDataWebApp)Application.get()).store().getSession("init");

            final String csvIndexProperties = Optional.ofNullable(getInitParameter("index-properties")).orElse("name");
            this.idxProperties = Arrays.asList(csvIndexProperties.split(","));

            if (!indexExists("fulltextNode")) {
                final String queryNod = "CALL db.index.fulltext.createNodeIndex(\"fulltextNode\", $entities, $properties)";
                ogm.query(void.class, queryNod, Map.of("entities", this.store.namesNodes(), "properties", this.idxProperties));
            }
            if (!indexExists("fulltextRelationship")) {
                final String queryRel = "CALL db.index.fulltext.createRelationshipIndex(\"fulltextRelationship\", $entities, $properties)";
                ogm.query(void.class, queryRel, Map.of("entities", this.store.namesRelationships(), "properties", this.idxProperties));
            }
            ((GraphDataWebApp)Application.get()).store().dropSession("init");
        }
    }

    private boolean indexExists(final String name) {
        final org.neo4j.ogm.session.Session ogm = ((GraphDataWebApp)Application.get()).store().getSession("init");
        final String query = "CALL db.indexes() YIELD indexName WHERE indexName=$name RETURN COUNT(*)";
        final Iterable<Integer> result = ogm.query(int.class, query, Map.of("name", name));
        final int count = result.iterator().next();
        ((GraphDataWebApp)Application.get()).store().dropSession("init");
        return count > 0;
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        final ConverterLocator loc = new ConverterLocator();
        loc.set(ZonedDateTime.class, new IsoDateConverter());
        return loc;
    }

    public Props props() {
        return this.props;
    }

    public Store store() {
        return this.store;
    }

    public String title() {
        return this.title;
    }

    public String stylesheet() {
        return this.stylesheet;
    }
}

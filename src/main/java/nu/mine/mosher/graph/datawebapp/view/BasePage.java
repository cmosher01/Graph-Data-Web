package nu.mine.mosher.graph.datawebapp.view;

import nu.mine.mosher.graph.datawebapp.GraphDataWebApp;
import nu.mine.mosher.graph.datawebapp.store.Store;
import nu.mine.mosher.graph.datawebapp.util.Props;
import org.apache.wicket.*;
import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.head.*;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import java.time.*;
import java.util.*;

public abstract class BasePage extends WebPage {
    public BasePage() {
        add(new Label("title", app().title()));
        add(new Link<Void>("home") {
            @Override
            public void onClick() {
                setResponsePage(Application.get().getHomePage());
            }
        });
        add(new Label("timeStamp", ZonedDateTime.now(ZoneOffset.UTC)));
    }

    @Override
    public MarkupType getMarkupType() {
        return new MarkupType(".xml", MarkupType.XML_MIME);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        if (!app().stylesheet().isEmpty()) {
            response.render(CssHeaderItem.forUrl(app().stylesheet()));
        }
    }

    protected org.neo4j.ogm.session.Session ogm() {
        return store().getSession(getSession().getId());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected static List search(final String anytext, final Class cls) {
        final String query;
        // TODO implement paging
        if (appstatic().store().isNodeEntity(cls)) {
            query = "CALL db.index.fulltext.queryNodes(\"fulltextNode\", $anytext) YIELD node, score RETURN node, score ORDER BY node.id LIMIT 100";
        } else {
            query = "CALL db.index.fulltext.queryRelationships(\"fulltextRelationship\", $anytext) YIELD relationship AS r MATCH (n)-[r]-(m) RETURN n,r,m LIMIT 100";
        }
        final org.neo4j.ogm.session.Session ogm = appstatic().store().getSession(Session.get().getId());
        final Iterable resultset = ogm.query(cls, query, Map.of("anytext", anytext));
        final List result = new ArrayList<>();
        resultset.forEach(result::add);
        return result;
    }

    protected Store store() {
        return app().store();
    }

    protected Props props() {
        return app().props();
    }

    protected GraphDataWebApp app() {
        return appstatic();
    }

    private static GraphDataWebApp appstatic() {
        return (GraphDataWebApp)Application.get();
    }
}

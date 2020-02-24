package nu.mine.mosher.graph.datawebapp.view;

import nu.mine.mosher.graph.datawebapp.util.Utils;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.*;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PageList extends BasePage {
    public PageList(final Class cls) {
        this(cls, "", 0, 5);
    }

    private PageList(final Class cls, final String search, final int page, final int sizePage) {
        add(new Label("entity", cls.getSimpleName()));
        add(new SearchForm(cls, search, page, sizePage));
        add(new ListEntity(cls, search, page, sizePage));
//        add(new WebMarkupContainer("empty").setVisible(!store().any(cls)));
        add(new LinkNew(cls));
        add(new LinkPage("prev", cls, page-1, search, sizePage).setEnabled(0 < page));
        add(new Label("page", new StringResourceModel("page", Model.of(new Page(page)))));
        add(new LinkPage("next", cls, page+1, search, sizePage));
    }

    private static class LinkPage extends Link<Void> {
        private final Class c;
        private int page;
        private final String search;
        private final int sizePage;
        public LinkPage(final String id, final Class c, int page, final String search, int sizePage) {
            super(id);
            this.c = c;
            this.page = page;
            this.search = search;
            this.sizePage = sizePage;
        }

        @Override
        public void onClick() {
            setResponsePage(new PageList(c, search, page, sizePage));
        }
    }

    private static class Page implements Serializable {
        int page;
        Page(int p) { page = p+1; }
    }

    private static List<Serializable> recent(Class cls) {
        // TODO MRU search
        return Collections.emptyList();
    }

    private static final class ListEntity extends PropertyListView<Serializable> {
        public ListEntity(Class c, final String s, int page, int sizePage) {
            super("list", s.isEmpty() ? recent(c) : search(s, c, page, sizePage));
        }

        @Override
        protected void populateItem(final ListItem item) {
            item.add(new LinkEntity((Serializable)item.getModelObject()));
        }
    }

    private static final class LinkEntity extends Link<Void> {
        private final Serializable entity;
        public LinkEntity(final Serializable entity) {
            super("link");
            this.entity = entity;
            add(new Label("entity", Utils.str(entity)));
        }

        @Override
        public void onClick() {
            setResponsePage(new PageView(entity.getClass(), Utils.id(entity), Utils.uuid(entity)));
        }
    }

    private static final class LinkNew extends Link<Void> {
        private final Class c;
        public LinkNew(Class c) {
            super("new");
            this.c = c;
        }

        @Override
        public void onClick() {
            setResponsePage(new PageEdit(c, null, null));
        }

    }

    private static class SearchForm extends Form<Void> {
        private final Class c;
        private String search;
        private int page;
        private int sizePage;
        public SearchForm(final Class c, final String search, int page, int sizePage) {
            super("form");
            this.c = c;
            this.search = search;
            this.page = page;
            this.sizePage = sizePage;
            setDefaultModel(new CompoundPropertyModel<>(this));
            add(new TextField<String>("search"));
        }

        @Override
        protected void onSubmit() {
            setResponsePage(new PageList(c, search, 0, sizePage));
        }
    }




    @SuppressWarnings({"rawtypes", "unchecked"})
    private static List search(final String anytext, final Class cls, int page, int sizePage) {
        if (page < 0) {
            return Collections.emptyList();
        }

        String query;
        // TODO implement paging
        if (Utils.store().isNodeEntity(cls)) {
            query =
                "CALL db.index.fulltext.queryNodes(\"fulltextNode\", $anytext) " +
                "YIELD node AS x " +
                "WHERE x:"+cls.getSimpleName()+" " +
                "RETURN x";
        } else {
            query =
                "CALL db.index.fulltext.queryRelationships(\"fulltextRelationship\", $anytext) " +
                "YIELD relationship AS x " +
                "MATCH (n)-[x:"+cls.getSimpleName()+"]-(m) " +
                "RETURN n,x,m";
        }
        query += " ORDER BY x.id SKIP $skip LIMIT $sizePage";

        final org.neo4j.ogm.session.Session ogm = Utils.store().getSession(Session.get().getId());
        final Iterable resultset = ogm.query(cls, query,
            Map.of("anytext", anytext,
                "skip", page*sizePage,
                "sizePage", sizePage));
        final List result = new ArrayList<>();
        resultset.forEach(result::add);
        return result;
    }
}

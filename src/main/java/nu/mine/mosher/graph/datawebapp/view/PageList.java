package nu.mine.mosher.graph.datawebapp.view;

import nu.mine.mosher.graph.datawebapp.util.Utils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.*;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PageList extends SecurePage {
    public PageList(final Class cls) {
        this(cls, "", 0, 50);
    }

    private PageList(final Class cls, final String search, final int page, final int sizePage) {
        final List<Serializable> entities = loadList(cls, search, page, sizePage);

        add(new Label("entity", cls.getSimpleName()));
        add(new SearchForm(cls, search, sizePage));
        add(new ListEntity(entities));
        add(new WebMarkupContainer("empty").setVisible(entities.size() < sizePage));
        add(new LinkNew(cls));
        add(new LinkPage("prev", cls, page-1, search, sizePage).setEnabled(0 < page));
        add(new Label("page", new StringResourceModel("page", Model.of(new Page(page)))));
        add(new LinkPage("next", cls, page+1, search, sizePage).setEnabled(sizePage <= entities.size()));
    }

    private static class Page implements Serializable {
        int page;
        Page(int p) { page = p+1; }
    }



    private static class LinkPage extends Link<Void> {
        private final Class cls;
        private int page;
        private final String search;
        private final int sizePage;
        public LinkPage(final String id, final Class cls, int page, final String search, int sizePage) {
            super(id);
            this.cls = cls;
            this.page = page;
            this.search = search;
            this.sizePage = sizePage;
        }

        @Override
        public void onClick() {
            setResponsePage(new PageList(cls, search, page, sizePage));
        }
    }



    private static final class ListEntity extends PropertyListView<Serializable> {
        public ListEntity(List<Serializable> entities) {
            super("list", entities);
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
        private final Class cls;
        public LinkNew(final Class cls) {
            super("new");
            this.cls = cls;
        }

        @Override
        public void onClick() {
            setResponsePage(new PageEdit(cls, null, null));
        }

    }



    private static class SearchForm extends Form<Void> {
        private final Class c;
        private String search;
        private int sizePage;
        public SearchForm(final Class c, final String search, final int sizePage) {
            super("form");
            this.c = c;
            this.search = search;
            this.sizePage = sizePage;
            setDefaultModel(new CompoundPropertyModel<>(this));
            add(new TextField<String>("search"));
        }

        @Override
        protected void onSubmit() {
            setResponsePage(new PageList(c, search, 0, sizePage));
        }
    }





    private static List<Serializable> loadList(final Class cls, final String search, int page, int sizePage) {
        return search.isEmpty() ? recent(cls) : search(search, cls, page, sizePage);
    }

    private static List<Serializable> recent(Class cls) {
        // TODO MRU search
        return Collections.emptyList();
    }

    private static List<Serializable> search(final String anytext, final Class cls, int page, int sizePage) {
        if (page < 0) {
            return Collections.emptyList();
        }

        final Iterable<Serializable> resultset =
            Utils.ogm().query(
                cls,
                getNodeOrRelationshipQuery(cls),
                Map.of("anytext", anytext,
                    "skip", page*sizePage,
                    "sizePage", sizePage));

        final List<Serializable> result = new ArrayList<>();
        resultset.forEach(result::add);
        return result;
    }

    private static String getNodeOrRelationshipQuery(Class cls) {
        String query;

        if (Utils.store().isNodeEntity(cls)) {
            query =
                "CALL db.index.fulltext.queryNodes('fulltextNode', $anytext) " +
                "YIELD node AS x " +
                "WHERE x:"+cls.getSimpleName()+" " +
                "RETURN x ";
        } else {
            query =
                "CALL db.index.fulltext.queryRelationships('fulltextRelationship', $anytext) " +
                "YIELD relationship AS x " +
                "MATCH (n)-[x:"+cls.getSimpleName()+"]-(m) " +
                "RETURN n,x,m ";
        }

        query +=
            "ORDER BY x.id " +
            "SKIP $skip " +
            "LIMIT $sizePage";

        return query;
    }
}

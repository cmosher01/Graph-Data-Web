package nu.mine.mosher.graph.datawebapp.view;

import nu.mine.mosher.graph.datawebapp.util.Utils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.CompoundPropertyModel;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PageList extends BasePage {
    public PageList(final Class cls) {
        this(cls, "");
    }

    public PageList(final Class cls, final String search) {
        add(new Label("entity", cls.getSimpleName()));
        add(new SearchForm(cls));
        add(new ListEntity(cls, search));
        add(new WebMarkupContainer("empty").setVisible(!store().any(cls)));
        add(new LinkNew(cls));
    }

    private static List<Serializable> recent(Class cls) {
        // TODO MRU search
        return Collections.emptyList();
    }

    private static final class ListEntity extends PropertyListView<Serializable> {
        public ListEntity(Class c, final String s) {
            super("list", s.isEmpty() ? recent(c) : search(s, c));
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
        public SearchForm(Class c) {
            super("form");
            this.c = c;
            this.search = "";
            setDefaultModel(new CompoundPropertyModel<>(this));
            add(new TextField<String>("search"));
        }

        @Override
        protected void onSubmit() {
            setResponsePage(new PageList(c, search));
        }
    }
}

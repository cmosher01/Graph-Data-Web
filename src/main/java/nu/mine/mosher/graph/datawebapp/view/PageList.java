package nu.mine.mosher.graph.datawebapp.view;

import nu.mine.mosher.graph.datawebapp.util.Utils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.*;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PageList extends BasePage {
    private final Class cls;

    public PageList(final Class cls) {
        this.cls = Objects.requireNonNull(cls);
        add(new Label("entity", cls.getSimpleName()));
        add(new ListEntity());
        add(new WebMarkupContainer("empty").setVisible(!store().any(cls)));
        add(new LinkNew());
    }

    private Collection getAll() {
        return store().getAll(cls, 1); // TODO implement pagination
    }

    private final class ListEntity extends PropertyListView<Serializable> {
        public ListEntity() {
            super("list", Collections.list(Collections.enumeration(getAll())));
        }

        @Override
        protected void populateItem(final ListItem item) {
            item.add(new LinkEntity((Serializable)item.getModelObject()));
        }
    }

    private final class LinkEntity extends Link<Void> {
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

    private final class LinkNew extends Link<Void> {
        public LinkNew() {
            super("new");
        }

        @Override
        public void onClick() {
            setResponsePage(new PageEdit(cls, null, null));
        }
    }
}

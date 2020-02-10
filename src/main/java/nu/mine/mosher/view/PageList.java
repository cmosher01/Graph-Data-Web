package nu.mine.mosher.view;

import nu.mine.mosher.app.App;
import nu.mine.mosher.store.Store;
import nu.mine.mosher.util.Utils;
import org.apache.wicket.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PageList extends BasePage {
    private final Class cls;

    public PageList(final Class cls) {
        this.cls = Objects.requireNonNull(cls);
        add(new Label("entity", cls.getSimpleName()));
        add(new ListEntity());
        add(new Label("empty", Model.of("[none]")).setVisible(store().count(cls) == 0L));
        add(new LinkNew());
    }

    private Collection getAll() {
        return store().getAll(cls);
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
        private final Long id;
        public LinkEntity(final Serializable entity) {
            super("link");
            this.id = Utils.id(entity);
            add(new Label("entity", Utils.str(entity)));
        }

        @Override
        public void onClick() {
            setResponsePage(new PageEdit(cls, id));
        }
    }

    private final class LinkNew extends Link<Void> {
        public LinkNew() {
            super("new");
        }

        @Override
        public void onClick() {
            setResponsePage(new PageEdit(cls, null));
        }
    }

    private static Store store() {
        return ((App)Application.get()).store();
    }
}

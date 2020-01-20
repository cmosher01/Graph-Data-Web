package nu.mine.mosher.view;

import nu.mine.mosher.app.App;
import nu.mine.mosher.app.sample.Sample;
import nu.mine.mosher.store.Store;
import org.apache.wicket.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebSession;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PageList extends BasePage {
    private final Class cls;

    public PageList(final Class cls) {
        this.cls = Objects.requireNonNull(cls);
        add(new Label("entity", cls.getSimpleName()));
        add(new ListEntity());
        add(new Label("empty", Model.of("[none]")).setVisible(store().count(cls, Session.get().getId()) == 0L));
        add(new LinkNew());
    }

    private Collection getAll() {
        Collection all = store().getAll(cls, Session.get().getId());
        if (all.isEmpty()) {
            Sample.create((App)Application.get());
            all = store().getAll(cls, Session.get().getId());
        }
        return all;
    }

    private final class ListEntity extends PropertyListView<Serializable> {
        public ListEntity() {
            super("list", Collections.list(Collections.enumeration(getAll())));
        }

        @Override
        protected void populateItem(final ListItem item) {
            item.add(new LinkEntity((Serializable)item.getModelObject()));
        }

        private final class LinkEntity extends Link<Void> {
            private final Serializable entity;
            public LinkEntity(final Serializable entity) {
                super("link");
                this.entity = entity;
                add(new Label("display", entity.toString()));
            }

            @Override
            public void onClick() {
                setResponsePage(new PageEdit(entity));
            }
        }
    }

    private final class LinkNew extends Link<Void> {
        public LinkNew() {
            super("new");
        }

        @Override
        public void onClick() {
            setResponsePage(new PageEdit(Store.create(cls)));
        }
    }

    private static Store store() {
        return ((App)Application.get()).store();
    }
}

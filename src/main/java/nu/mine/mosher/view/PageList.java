package nu.mine.mosher.view;

import nu.mine.mosher.app.App;
import nu.mine.mosher.app.sample.Sample;
import nu.mine.mosher.store.Store;
import org.apache.wicket.Application;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.*;

import java.util.*;

public class PageList extends BasePage {
    private final Class cls;

    public PageList(final Class cls) {
        this.cls = Objects.requireNonNull(cls);
        add(new Label("entity", cls.getSimpleName()));
        add(new ListEntity());
        add(new Label("empty", Model.of("[none]")).setVisible(store().count(cls) == 0));
        add(new LinkNew());
    }

    private List getAll() {
        List all = store().getAll(cls);
        if (all.isEmpty()) {
            Sample.create((App)Application.get());
            all = store().getAll(cls);
        }
        return all;
    }

    private final class ListEntity extends PropertyListView {
        public ListEntity() {
            super("list", getAll());
        }

        @Override
        protected void populateItem(final ListItem item) {
            item.add(new LinkEntity(item.getModelObject()));
        }

        private final class LinkEntity extends Link<Void> {
            private final Long id;
            public LinkEntity(final Object entity) {
                super("link");
                id = (Long)new PropertyModel<>(entity, "id").getObject();
                add(new Label("display", new PropertyModel<>(entity, "display")));
            }

            @Override
            public void onClick() {
                setResponsePage(new PageEdit(cls, id));
            }
        }
    }

    private final class LinkNew extends Link<Void> {
        public LinkNew() {
            super("new");
        }

        @Override
        public void onClick() {
            setResponsePage(new PageEdit(cls, 0L));
        }
    }

    private static Store store() {
        return ((App)Application.get()).store();
    }
}

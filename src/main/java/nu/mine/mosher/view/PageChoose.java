package nu.mine.mosher.view;

import nu.mine.mosher.app.App;
import nu.mine.mosher.store.Store;
import nu.mine.mosher.util.*;
import org.apache.wicket.Application;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.*;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class PageChoose extends BasePage {
    private final Serializable parent;
    private final Props.Ref ref;

    public PageChoose(Serializable entity, Props.Ref ref, List candidates) {
        this.parent = entity;
        this.ref = ref;
        add(new Label("entity", ref.name));
        add(new ListEntity(candidates));
        add(new Label("empty", Model.of("[none]")).setVisible(store().count(ref.cls) == 0L));
//        add(new LinkNew());
    }



    private final class ListEntity extends PropertyListView {
        public ListEntity(List candidates) {
            super("list", candidates);
        }

        @Override
        protected void populateItem(final ListItem item) {
            item.add(new LinkEntity(item.getModelObject()));
        }

        private final class LinkEntity extends Link<Void> {
            private final Object entity;
            public LinkEntity(final Object entity) {
                super("link");
                this.entity = entity;
                add(new Label("display", Utils.str(entity)));
            }

            @Override
            public void onClick() {
                if (ref.collection) {
                    ((Collection)new PropertyModel<>(parent, ref.name).getObject()).add(entity);
                } else {
                    new PropertyModel<>(parent, ref.name).setObject(entity);
                }
                setResponsePage(new PageEdit(parent));
            }
        }
    }


//    private final class LinkNew extends Link<Void> {
//        public LinkNew() {
//            super("new");
//        }
//
//        @Override
//        public void onClick() {
//            setResponsePage(new PageEdit(cls, 0L));
//        }
//    }



    private static Store store() {
        return ((App)Application.get()).store();
    }
}

package nu.mine.mosher.view;

import nu.mine.mosher.app.App;
import nu.mine.mosher.store.Store;
import nu.mine.mosher.util.*;
import org.apache.wicket.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.*;
import org.slf4j.*;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class PageChoose extends BasePage {
    private final Logger LOG = LoggerFactory.getLogger(PageChoose.class);

    private final transient org.neo4j.ogm.session.Session ogm;
    private final Serializable parent;
    private final Props.Ref ref;

    public PageChoose(Serializable entity, Props.Ref ref, Collection candidates, org.neo4j.ogm.session.Session ogm) {
        this.ogm = Objects.requireNonNull(ogm);
        this.parent = entity;
        this.ref = ref;
        add(new Label("entity", ref.name+":"+ref.cls.getSimpleName()));
        add(new ListEntity(candidates));
        add(new Label("empty", Model.of("[none]")).setVisible(store().count(ref.cls) == 0L));
//        add(new LinkNew());
        add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                setResponsePage(new PageView(parent, ogm));
            }
        });
    }



    private final class ListEntity extends PropertyListView<Serializable> {
        public ListEntity(Collection candidates) {
            super("list", Collections.list(Collections.enumeration(candidates)));
        }

        @Override
        protected void populateItem(final ListItem<Serializable> item) {
            item.add(new LinkEntity(item.getModelObject(), ogm));
        }

        private final class LinkEntity extends Link<Void> {
            private final transient org.neo4j.ogm.session.Session ogm;
            private final Serializable child;
            public LinkEntity(final Serializable child, org.neo4j.ogm.session.Session ogm) {
                super("link");
                this.child = child;
                this.ogm = ogm;
                add(new Label("display", Utils.str(child)));
            }

            @Override
            public void onClick() {
                if (ref.collection) {
                    ((Collection)new PropertyModel<>(parent, ref.name).getObject()).add(child);
                    // TODO
                } else {
                    new PropertyModel<>(parent, ref.name).setObject(child);
                    ogm.delete(parent);
                }
                try {
                    ogm.save(Utils.resetEntity(parent));
                    setResponsePage(new PageView(parent.getClass(), Utils.uuid(parent), store().createSession()));
                } catch (Throwable e) {
                    e.printStackTrace();
                    setResponsePage(new PageView(parent, ogm));
                }
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
//            TODO how will this work?
//            setResponsePage(new PageEdit(cls, null));
//        }
//    }



    private static Store store() {
        return ((App)Application.get()).store();
    }
}

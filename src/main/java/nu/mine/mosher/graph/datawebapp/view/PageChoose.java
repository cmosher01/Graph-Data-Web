package nu.mine.mosher.graph.datawebapp.view;

import nu.mine.mosher.graph.datawebapp.util.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class PageChoose extends SecurePage {
    private final Serializable parent;
    private final Props.Ref ref;

    public PageChoose(Serializable entity, Props.Ref ref) {
        this.parent = entity;
        this.ref = ref;
        add(new Label("entity", ref.name+":"+ref.cls.getSimpleName()));
        add(new ListEntity(recent(ref.cls)));
//        add(new WebMarkupContainer("empty").setVisible(!store().any(ref.cls)));
        add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                setResponsePage(new PageView(parent));
            }
        });
    }

    private static List<Serializable> recent(Class cls) {
        // TODO MRU search
        final org.neo4j.ogm.session.Session ogm = Utils.ogm();
        return Collections.list(Collections.enumeration(ogm.loadAll(cls)));
    }

    private final class ListEntity extends PropertyListView<Serializable> {
        public ListEntity(List<Serializable> candidates) {
            super("list", candidates);
        }

        @Override
        protected void populateItem(final ListItem<Serializable> item) {
            item.add(new LinkEntity(item.getModelObject()));
        }

        private final class LinkEntity extends Link<Void> {
            private final Serializable child;
            public LinkEntity(final Serializable child) {
                super("link");
                this.child = child;
                add(new Label("entity", Utils.str(child)));
            }

            @Override
            public void onClick() {
                if (ref.collection) {
                    ((Collection)new PropertyModel<>(parent, ref.name).getObject()).add(child);
                } else {
                    new PropertyModel<>(parent, ref.name).setObject(child);
                }
                try {
                    Utils.ogm().save(Utils.resetEntity(parent));
                    Utils.store().dropSession();
                    setResponsePage(new PageView(parent.getClass(), Utils.id(parent), Utils.uuid(parent)));
                } catch (Throwable e) {
                    e.printStackTrace();
                    setResponsePage(new PageView(parent));
                }
            }
        }
    }
}

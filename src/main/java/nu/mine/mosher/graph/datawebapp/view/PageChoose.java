package nu.mine.mosher.graph.datawebapp.view;

import nu.mine.mosher.graph.datawebapp.util.*;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.*;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class PageChoose extends BasePage {
    private final Logger LOG = LoggerFactory.getLogger(PageChoose.class);

    private final Serializable parent;
    private final Props.Ref ref;

    public PageChoose(Serializable entity, Props.Ref ref, Collection candidates) {
        this.parent = entity;
        this.ref = ref;
        add(new Label("entity", ref.name+":"+ref.cls.getSimpleName()));
        add(new ListEntity(candidates));
        add(new WebMarkupContainer("empty").setVisible(store().count(ref.cls) == 0L));
        add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                setResponsePage(new PageView(parent));
            }
        });
    }



    private final class ListEntity extends PropertyListView<Serializable> {
        public ListEntity(Collection candidates) {
            super("list", Collections.list(Collections.enumeration(candidates)));
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
                    ogm().save(Utils.resetEntity(parent));
                    store().dropSession(getSession().getId());
                    setResponsePage(new PageView(parent.getClass(), Utils.uuid(parent)));
                } catch (Throwable e) {
                    e.printStackTrace();
                    setResponsePage(new PageView(parent));
                }
            }
        }
    }
}

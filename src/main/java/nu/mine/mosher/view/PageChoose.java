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
import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class PageChoose extends BasePage {
    private final Logger LOG = LoggerFactory.getLogger(PageChoose.class);

    private final Serializable parent;
    private final Props.Ref ref;

    public PageChoose(Serializable entity, Props.Ref ref, Collection candidates) {
        this.parent = entity;
        this.ref = ref;
        add(new Label("entity", ref.name));
        add(new ListEntity(candidates));
        add(new Label("empty", Model.of("[none]")).setVisible(store().count(ref.cls) == 0L));
//        add(new LinkNew());
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
                add(new Label("display", Utils.str(child)));
            }

            @Override
            public void onClick() {
                if (ref.collection) {
                    ((Collection)new PropertyModel<>(parent, ref.name).getObject()).add(child);
//                    clearRef(child, parent);
                } else {
                    new PropertyModel<>(parent, ref.name).setObject(child);
                }
                setResponsePage(new PageEdit(parent.getClass(), Utils.uuid(parent)));
            }
        }
    }

//    private void clearRef(Serializable entity, Serializable parent) {
//        final Field xref = Arrays.
//            stream(entity.getClass().getDeclaredFields()).
//            filter(f -> parent.getClass().isAssignableFrom(f.getType())).
//            findAny().
//            orElseThrow();
//        xref.setAccessible(true);
//        try {
//            final Serializable orig = (Serializable)xref.get(entity);
//
//            LOG.info("preupdate: {}", entity);
//            LOG.info("preupdate: {}", orig);
//            xref.set(entity, parent);
//            ((Collection)new PropertyModel<>(orig, ref.name).getObject()).remove(entity);
//            LOG.info("updated 1: {}", entity);
//            LOG.info("updated 1: {}", orig);
//            store().save(entity);
//            LOG.info("updated 2: {}", entity);
//            LOG.info("updated 2: {}", orig);
//            store().save(orig);
//            LOG.info("updated 3: {}", entity);
//            LOG.info("updated 3: {}", orig);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }


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

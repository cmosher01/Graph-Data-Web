package nu.mine.mosher.graph.datawebapp.view;


import nu.mine.mosher.graph.datawebapp.util.Utils;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.*;

import java.util.List;


@SuppressWarnings({"rawtypes"})
public class PageHome extends SecurePage {
    public PageHome() {
        setVersioned(false);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        if (AuthenticatedWebSession.get().isSignedIn()) {
            add(new ListEntity());
        }
    }

    private final class ListEntity extends ListView<Class> {
        public ListEntity() {
            super("list", entities());
        }

        @Override
        protected void populateItem(final ListItem<Class> item) {
            item.add(new LinkEntity(item.getModelObject()));
        }
    }

    private static final class LinkEntity extends Link<Void> {
        private final Class cls;
        public LinkEntity(final Class cls) {
            super("link");
            this.cls = cls;
            add(new Label("entity", cls.getSimpleName()));
        }

        @Override
        public void onClick() {
            setResponsePage(new PageList(cls));
        }
    }

    private List<Class> entities() {
        return List.copyOf(Utils.store().entityClasses());
    }
}

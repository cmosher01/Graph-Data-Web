package nu.mine.mosher.view;


import nu.mine.mosher.app.sample.model.Models;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.PropertyModel;

import java.util.List;


@SuppressWarnings({"rawtypes"})
public class PageHome extends BasePage {
    public PageHome() {
        add(new ListEntity());
    }

    private static final class ListEntity extends ListView<Class> {
        public ListEntity() {
            super("list", entities());
        }

        @Override
        protected void populateItem(final ListItem<Class> item) {
            item.add(new LinkEntity(item.getModelObject()));
        }

        private static final class LinkEntity extends Link<Void> {
            private final Class cls;
            public LinkEntity(final Class cls) {
                super("link");
                this.cls = cls;
                add(new Label("entity", new PropertyModel<>(cls, "simpleName")));
            }

            @Override
            public void onClick() {
                setResponsePage(new PageList(cls));
            }
        }
    }

    private static List<Class> entities() {
        return List.copyOf(Models.entities());
    }
}

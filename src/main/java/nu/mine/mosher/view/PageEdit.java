package nu.mine.mosher.view;

import nu.mine.mosher.store.Store;
import nu.mine.mosher.util.Utils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.*;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PageEdit extends BasePage {
    private final Serializable entity;
    private boolean isNew;
    private boolean isBad;

    public PageEdit(Class cls, UUID uuid) {
        this.isNew = Objects.isNull(uuid);
        this.entity = this.isNew ? Store.create(cls) : (Serializable)ogm().load(cls, uuid);
        add(new Label("entity", cls.getSimpleName()));
        add(new FormEntity());
    }

    private final class FormEntity extends Form<Serializable> {
        public FormEntity() {
            super("form", Model.of(entity));



            add(new ListView<>("properties", props().properties(entity.getClass())) {
                @Override
                protected void populateItem(final ListItem<String> item) {
                    final String nameProperty = item.getModelObject();

                    final Component name = new Label("name", nameProperty).setRenderBodyOnly(true);

                    final PropertyModel model = new PropertyModel<>(entity, nameProperty);
                    final LabeledWebMarkupContainer property = new TextField<String>("property", model);

                    final FormComponentLabel label = new FormComponentLabel("label", property);
                    label.add(name);
                    label.add(property);

                    item.add(label);
                }
            }.setReuseItems(true));



            add(new SubmitLink("cancel") {
                @Override
                public void onSubmit() {
                    next();
                }
            }.setDefaultFormProcessing(false));
        }

        @Override
        protected void onSubmit() {
            isBad = true;
            try {
                ogm().save(entity);
                store().dropSession(getSession().getId());
                isNew = false;
                isBad = false;
            } catch (Throwable e) {
                e.printStackTrace();
            }
            next();
        }

        private void next() {
            if (isBad) {
                // maybe it just needs links, so take them to the view-link page
                setResponsePage(new PageView(entity));
            } else if (isNew) {
                setResponsePage(new PageList(entity.getClass()));
            } else {
                setResponsePage(new PageView(entity.getClass(), Utils.uuid(entity)));
            }
        }
    }
}

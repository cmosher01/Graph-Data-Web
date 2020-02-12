package nu.mine.mosher.view;

import nu.mine.mosher.app.App;
import nu.mine.mosher.store.Store;
import nu.mine.mosher.util.*;
import org.apache.wicket.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.*;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PageEdit extends BasePage {
    public PageEdit(Class cls, UUID uuid, org.neo4j.ogm.session.Session ogm) {
        this.ogm = Objects.requireNonNull(ogm);
        this.isNew = Objects.isNull(uuid);
        this.entity = this.isNew ? Store.create(cls) : (Serializable)ogm.load(cls, uuid);
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



            add(new SubmitLink("save") {
                @Override
                public void onSubmit() {
                    save();
                    next();
                }
            });
        }

        private void save() {
            isBad = true;
            try {
                ogm.save(entity);
                isNew = false;
                isBad = false;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        private void next() {
            if (isBad) {
                // maybe it just needs links, so take them to the view-link page
                setResponsePage(new PageView(entity, ogm));
            } else if (isNew) {
                setResponsePage(new PageList(entity.getClass()));
            } else {
                setResponsePage(new PageView(entity.getClass(), Utils.uuid(entity), store().createSession()));
            }
        }
    }

    private static Store store() {
        return ((App)Application.get()).store();
    }

    private static Props props() {
        return ((App)Application.get()).props();
    }

    private final Serializable entity;
    private boolean isNew;
    private boolean isBad;
    private final transient org.neo4j.ogm.session.Session ogm;
}

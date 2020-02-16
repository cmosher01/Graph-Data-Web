package nu.mine.mosher.graph.datawebapp.view;

import nu.mine.mosher.graph.datawebapp.util.*;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PageEdit extends BasePage {
    private final Serializable entity;
    private boolean isNew;
    private boolean isBad;

    public PageEdit(Class cls, UUID uuid) {
        this.isNew = Objects.isNull(uuid);
        this.entity = this.isNew ? Utils.create(cls) : (Serializable)ogm().load(cls, uuid);
        add(new Label("entity", cls.getSimpleName()));
        add(new FormEntity());
    }

    private final class FormEntity extends Form<Serializable> {
        public FormEntity() {
            super("form", Model.of(entity));



            add(new ListView<>("properties", props().properties(entity.getClass())) {
                @Override
                protected void populateItem(final ListItem<Props.Prop> item) {
                    final Props.Prop prop = item.getModelObject();

                    final Component name = new Label("name", prop.name);
                    item.add(name);

                    final PropertyModel model = new PropertyModel<>(entity, prop.name);
                    final LabeledWebMarkupContainer property;
                    final Fragment fragment;
                    if (prop.isBoolean) {
                        fragment = new Fragment("entry", "entryCheckbox", FormEntity.this);
                        property = new CheckBox("property", model);
                    } else {
                        fragment = new Fragment("entry", "entryText", FormEntity.this);
                        property = new TextField<String>("property", model);
                    }
                    if (prop.readOnly) {
                        property.setEnabled(false);
                    }
                    fragment.add(property);

                    item.add(fragment);
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

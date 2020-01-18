package nu.mine.mosher.view;

import nu.mine.mosher.app.App;
import nu.mine.mosher.store.Store;
import nu.mine.mosher.util.Props;
import org.apache.wicket.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.*;

import java.io.Serializable;
import java.util.*;

public class PageEdit extends BasePage {
    private final Class cls;
    private final long id;

    public PageEdit(Class cls, long id) {
        this.cls = cls;
        this.id = id;
        add(new Label("entity", cls.getSimpleName()));
        add(new FormEntity());
    }

    private final class FormEntity extends Form<Serializable> {
        public FormEntity() {
            super("form", Model.of(store().load(cls, id)));





            add(new ListView<>("properties", props().properties(cls)) {
                @Override
                protected void populateItem(final ListItem<String> item) {
                    final String sPropName = item.getModelObject();

                    final Component name = new Label("name", sPropName).setRenderBodyOnly(true);

                    final Serializable entity = FormEntity.this.getModelObject();
                    final PropertyModel model = new PropertyModel<>(entity, sPropName);
                    final LabeledWebMarkupContainer property = new TextField<String>("property", model);

                    final FormComponentLabel label = new FormComponentLabel("label", property);
                    label.add(name);
                    label.add(property);

                    item.add(label);
                }
            }.setReuseItems(true));





            add(new ListView<>("refsMultiple", props().refsMultiple(cls)) {
                @Override
                protected void populateItem(final ListItem<Props.Ref> item) {
                    final Props.Ref ref = item.getModelObject();
                    final String sPropName = ref.name;

                    final Component name = new Label("name", sPropName).setRenderBodyOnly(true);
                    item.add(name);

                    final Serializable entity = FormEntity.this.getModelObject();

                    final Collection referents = (Collection)new PropertyModel<>(entity, sPropName).getObject();
                    item.add(new ReferenceListView(entity, sPropName, referents));
                    item.add(new Label("empty", Model.of("[none]")).setVisible(referents.size() == 0L));

                    item.add(new SubmitLink("add") {
                        @Override
                        public void onSubmit() {
                            setResponsePage(new PageChoose(entity, ref, store().getAll(ref.cls)));
                        }
                    });
                }

                class ReferenceListView extends ListView {
                    private final String sPropName;
                    private final Serializable entity;
                    public ReferenceListView(Serializable entity, String sPropName, Collection referents) {
                        super("ref", List.copyOf(referents));
                        this.sPropName = sPropName;
                        this.entity = entity;
                    }

                    @Override
                    protected void populateItem(final ListItem item) {
                        item.add(new Label("display", item.getModelObject().toString()));
                        // implement "remove" link
                    }
                }
            });





            add(new ListView<>("refsSingular", props().refsSingular(cls)) {
                @Override
                protected void populateItem(final ListItem<Props.Ref> item) {
                    final Props.Ref ref = item.getModelObject();
                    final String sPropName = ref.name;

                    final Component name = new Label("name", sPropName).setRenderBodyOnly(true);
                    item.add(name);

                    final Serializable entity = FormEntity.this.getModelObject();

                    final Object referent = new PropertyModel<>(entity, sPropName).getObject();

                    item.add(new LinkEntity(referent).setVisible(Objects.nonNull(referent)));
                    item.add(new Label("empty", Model.of("[none]")).setVisible(Objects.isNull(referent)));

                    item.add(new SubmitLink("add") {
                        @Override
                        public void onSubmit() {
                            save();
                            setResponsePage(new PageChoose(entity, ref, store().getAll(ref.cls)));
                        }
                    }.setVisible(Objects.isNull(referent)));

                    item.add(new SubmitLink("remove") {
                        @Override
                        public void onSubmit() {
                            save();
                            new PropertyModel<>(entity, ref.name).setObject(null);
                        }
                    }.setVisible(Objects.nonNull(referent)));
                }

                final class LinkEntity extends SubmitLink {
                    private Long id;
                    private Class cls;
                    public LinkEntity(final Object entity) {
                        super("link");
                        if (Objects.nonNull(entity)) {
                            id = (Long)new PropertyModel<>(entity, "id").getObject();
                            cls = entity.getClass();
                        }
                        add(new Label("display", new PropertyModel<>(entity, "display")));
                    }

                    @Override
                    public void onSubmit() {
                        save();
                        setResponsePage(new PageEdit(cls, id));
                    }
                }
            });





            add(new SubmitLink("delete") {
                @Override
                public void onSubmit() {
                    store().delete(cls, id);
                    next();
                }
            }.setDefaultFormProcessing(false));
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
            store().save(FormEntity.this.getModelObject());
        }

        private void next() {
            setResponsePage(new PageList(cls));
        }
    }


    private static Store store() {
        return ((App)Application.get()).store();
    }

    private static Props props() {
        return ((App)Application.get()).props();
    }
}

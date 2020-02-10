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
    public PageEdit(Class cls, Long id) {
        this.entity = Objects.isNull(id) ? Store.create(cls) : Objects.requireNonNull(store().load(cls, id));
        add(new Label("entity", cls.getSimpleName()));
        add(new FormEntity());
    }

    private final class FormEntity extends Form<Serializable> {
        public FormEntity() {
            super("form", Model.of(entity));





            add(new ListView<>("properties", props().properties(entity.getClass())) {
                @Override
                protected void populateItem(final ListItem<String> item) {
                    final String sPropName = item.getModelObject();

                    final Component name = new Label("name", sPropName).setRenderBodyOnly(true);

                    final PropertyModel model = new PropertyModel<>(entity, sPropName);
                    final LabeledWebMarkupContainer property = new TextField<String>("property", model);

                    final FormComponentLabel label = new FormComponentLabel("label", property);
                    label.add(name);
                    label.add(property);

                    item.add(label);
                }
            }.setReuseItems(true));





            add(new ListView<>("refsMultiple", props().refsMultiple(entity.getClass())) {
                @Override
                protected void populateItem(final ListItem<Props.Ref> item) {
                    final Props.Ref ref = item.getModelObject();
                    final String sPropName = ref.name;

                    final Component name = new Label("name", sPropName).setRenderBodyOnly(true);
                    item.add(name);

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

                class ReferenceListView extends ListView<Serializable> {
                    private final String sPropName;
                    private final Serializable entity;
                    public ReferenceListView(Serializable entity, String sPropName, Collection referents) {
                        super("ref", List.copyOf(referents));
                        this.sPropName = sPropName;
                        this.entity = entity;
                    }

                    @Override
                    protected void populateItem(final ListItem<Serializable> item) {
                        // TODO link
                        final Serializable referent = item.getModelObject();
                        item.add(new LinkEntity(referent).setVisible(Objects.nonNull(referent)));
                        // TODO implement "remove" link
                    }
                }
            });





            add(new ListView<>("refsSingular", props().refsSingular(entity.getClass())) {
                @Override
                protected void populateItem(final ListItem<Props.Ref> item) {
                    final Props.Ref ref = item.getModelObject();

                    final Component name = new Label("name", ref.name).setRenderBodyOnly(true);
                    item.add(name);

                    final Serializable referent = (Serializable)new PropertyModel<>(entity, ref.name).getObject();
                    // TODO how to handle if referent is a relation (edge) type?

                    item.add(new LinkEntity(referent).setVisible(Objects.nonNull(referent)));
                    item.add(new Label("empty", Model.of("[none]")).setVisible(Objects.isNull(referent)));

                    item.add(new SubmitLink("add") {
                        @Override
                        public void onSubmit() {
                            save();
                            setResponsePage(new PageChoose(entity, ref, /* TODO limit list to choose from? */ store().getAll(ref.cls)));
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

            });





            add(new SubmitLink("delete") {
                @Override
                public void onSubmit() {
                    store().delete(entity);
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
            store().save(entity);
        }

        private void next() {
            setResponsePage(new PageList(entity.getClass()));
        }



        private final class LinkEntity extends SubmitLink {
            private final Serializable referent;
            public LinkEntity(final Serializable referent) {
                super("link");
                this.referent = referent;
                add(new Label("display", Utils.str(referent)));
            }

            @Override
            public void onSubmit() {
                save();
                setResponsePage(new PageEdit(this.referent.getClass(), Utils.id(this.referent)));
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
}

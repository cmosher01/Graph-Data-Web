package nu.mine.mosher.graph.datawebapp.view;

import nu.mine.mosher.graph.datawebapp.util.*;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PageView extends BasePage {
    private final Serializable entity;

    public PageView(Class cls, Long id, UUID uuid) {
        this.entity = Utils.same(uuid, Utils.ogm().load(cls,Objects.requireNonNull(id)));
        init();
    }

    public PageView(Serializable entity) {
        this.entity = Objects.requireNonNull(entity);
        init();
    }

    private void init() {
        add(new Link<Void>("list") {
            @Override
            public void onClick() {
                setResponsePage(new PageList(entity.getClass()));
            }
        });



        add(new Label("entity", entity.getClass().getSimpleName()));



        add(new Link<Void>("edit") {
            @Override
            public void onClick() {
                setResponsePage(new PageEdit(entity.getClass(), Utils.id(entity), Utils.uuid(entity)));
            }
        });

        add(new ListView<>("properties", Utils.props().properties(entity.getClass())) {
            @Override
            protected void populateItem(final ListItem<Props.Prop> item) {
                final String nameProperty = item.getModelObject().name;
                item.add(new Label("name", nameProperty));
                item.add(new Label("property", new PropertyModel<>(entity, nameProperty)));
            }
        }.setReuseItems(true));





        add(new ListView<>("refsMultiple", Utils.props().refsMultiple(entity.getClass())) {
            @Override
            protected void populateItem(final ListItem<Props.Ref> item) {
                final Props.Ref ref = item.getModelObject();

                final Component name = new Label("name", ref.name).setRenderBodyOnly(true);
                item.add(name);

                final Collection referents = (Collection)new PropertyModel<>(entity, ref.name).getObject();
                item.add(new ReferenceListView(ref, referents));
                item.add(new WebMarkupContainer("empty").setVisible(referents.size() == 0L));

                // A relationship entity can never already be in existence, so we always need
                // to make a new one here, rather than go to PageChooser
                // The downside, userability-wise, is the user needs to select both nodes for the
                // new relationship (even the node they're coming from) (TODO: fix that, somehow)
                item.add(new Link<Void>("add") {
                    @Override
                    public void onClick() {
                        setResponsePage(new PageEdit(ref.cls, null, null));
                    }
                });
            }

            class ReferenceListView extends ListView<Serializable> {
                private final Props.Ref ref;
                public ReferenceListView(Props.Ref ref, Collection referents) {
                    super("ref", List.copyOf(referents));
                    this.ref = ref;
                }

                @Override
                protected void populateItem(final ListItem<Serializable> item) {
                    final Serializable referent = item.getModelObject();
                    item.add(new LinkEntity(referent));
                    item.add(new LinkRemove(ref, referent));
                }
            }
        });





        add(new ListView<>("refsSingular", Utils.props().refsSingular(entity.getClass())) {
            @Override
            protected void populateItem(final ListItem<Props.Ref> item) {
                final Props.Ref ref = item.getModelObject();

                final Component name = new Label("name", ref.name).setRenderBodyOnly(true);
                item.add(name);

                final Serializable referent = (Serializable)new PropertyModel<>(entity, ref.name).getObject();

                item.add(new LinkEntity(referent).setVisible(Objects.nonNull(referent)));

                item.add(new LinkRemove(ref, referent).setVisible(Objects.nonNull(referent)));
                item.add(new WebMarkupContainer("empty").setVisible(Objects.isNull(referent)));

                item.add(new Link<Void>("add") {
                    @Override
                    public void onClick() {
                        setResponsePage(new PageChoose(entity, ref));
                    }
                }.setVisible(Objects.isNull(referent)));
            }
        });




        add(new Link<Void>("delete") {
            @Override
            public void onClick() {
                Utils.ogm().delete(entity);
                setResponsePage(new PageList(entity.getClass()));
            }
        });
    }

    private final class LinkRemove extends Link<Void> {
        private final Props.Ref ref;
        private final Serializable referent;
        public LinkRemove(Props.Ref ref, Serializable referent) {
            super("remove");
            this.ref = ref;
            this.referent = referent;
        }
        @Override
        public void onClick() {
            if (ref.collection) {
                ((Collection)new PropertyModel<>(entity, ref.name).getObject()).remove(referent);
            } else {
                new PropertyModel<>(entity, ref.name).setObject(null);
            }
            try {
                Utils.ogm().save(entity);
                setResponsePage(new PageView(entity.getClass(), Utils.id(entity), Utils.uuid(entity)));
            } catch (Throwable e) {
                e.printStackTrace();
                setResponsePage(new PageView(entity));
            }
        }
    }

    private static final class LinkEntity extends Link<Void> {
        private final Serializable referent;
        public LinkEntity(final Serializable referent) {
            super("link");
            this.referent = referent;
            add(new Label("entity", Utils.str(referent)));
        }

        @Override
        public void onClick() {
            setResponsePage(new PageView(this.referent.getClass(), Utils.id(this.referent), Utils.uuid(this.referent)));
        }
    }
}

package nu.mine.mosher.graph.datawebapp.view;

import nu.mine.mosher.graph.datawebapp.GraphDataWebApp;
import nu.mine.mosher.graph.datawebapp.store.Store;
import nu.mine.mosher.graph.datawebapp.util.Props;
import org.apache.wicket.Application;
import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;

public abstract class BasePage extends WebPage {
    public BasePage() {
        add(new Link<Void>("home") {
            @Override
            public void onClick() {
                setResponsePage(Application.get().getHomePage());
            }
        });
        add(new Label("timeStamp", ZonedDateTime.now(ZoneOffset.UTC)));
    }

    @Override
    public MarkupType getMarkupType() {
        return new MarkupType(".xml", MarkupType.XML_MIME);
    }

    protected org.neo4j.ogm.session.Session ogm() {
        return store().getSession(getSession().getId());
    }

    protected Store store() {
        return ((GraphDataWebApp)Application.get()).store();
    }

    protected Props props() {
        return ((GraphDataWebApp)Application.get()).props();
    }
}

package nu.mine.mosher.graph.datawebapp;

import nu.mine.mosher.graph.datawebapp.store.Store;
import nu.mine.mosher.graph.datawebapp.util.*;
import nu.mine.mosher.graph.datawebapp.view.PageHome;
import nu.mine.mosher.graph.sample.model.Event;
import org.apache.wicket.*;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

import java.time.ZonedDateTime;
import java.util.Optional;

public class GraphDataWebApp extends WebApplication {
    private Store store;
    private Props props;
    private String title;
    private String stylesheet;

    @Override
    public Class<? extends WebPage> getHomePage() {
        return PageHome.class;
    }

    @Override
    protected void init() {
        super.init();

        String csvPackageNames = Optional.ofNullable(getInitParameter("packages")).orElse(Event.class.getPackageName());
        this.store = new Store(csvPackageNames.split(","));
        this.props = new Props(this.store);
        this.title = Optional.ofNullable(getInitParameter("title")).orElse(csvPackageNames);
        this.stylesheet = Optional.ofNullable(getInitParameter("stylesheet")).orElse("");
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        final ConverterLocator loc = new ConverterLocator();
        loc.set(ZonedDateTime.class, new IsoDateConverter());
        return loc;
    }

    public Props props() {
        return this.props;
    }

    public Store store() {
        return this.store;
    }

    public String title() {
        return this.title;
    }

    public String stylesheet() {
        return this.stylesheet;
    }
}

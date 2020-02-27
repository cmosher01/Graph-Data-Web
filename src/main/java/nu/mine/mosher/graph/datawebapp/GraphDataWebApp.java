package nu.mine.mosher.graph.datawebapp;

import nu.mine.mosher.graph.datawebapp.store.Store;
import nu.mine.mosher.graph.datawebapp.util.Props;
import nu.mine.mosher.graph.datawebapp.view.*;
import nu.mine.mosher.graph.sample.imdb.Movie;
import org.apache.wicket.*;
import org.apache.wicket.authroles.authentication.*;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.convert.converter.ZonedDateTimeConverter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class GraphDataWebApp extends AuthenticatedWebApplication {
    private String csvPackageNames;
    private Store store;
    private Props props;
    private String title;
    private String stylesheet;

    @Override
    protected void init() {
        super.init();

        this.csvPackageNames = Optional.ofNullable(getInitParameter("packages")).orElse(Movie.class.getPackageName());
        this.store = new Store();
        this.props = new Props(this.store);
        this.title = Optional.ofNullable(getInitParameter("title")).orElse(this.csvPackageNames);
        this.stylesheet = Optional.ofNullable(getInitParameter("stylesheet")).orElse("");
    }


    @Override
    protected IConverterLocator newConverterLocator() {
        final ConverterLocator loc = new ConverterLocator();

        // override Wicket's built-in converter; we show ISO format instead
        loc.set(ZonedDateTime.class, new ZonedDateTimeConverter() {
            @Override
            protected DateTimeFormatter getDateTimeFormatter() {
                return DateTimeFormatter.ISO_ZONED_DATE_TIME;
            }
        });
        return loc;
    }

    @Override
    public Class<? extends WebPage> getHomePage() {
        return PageHome.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return PageLogin.class;
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass(){
        return Neo4jAuthenticatedSession.class;
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

    public String packages() {
        return this.csvPackageNames;
    }
}

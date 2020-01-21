package nu.mine.mosher.app;

import nu.mine.mosher.app.sample.model.Persona;
import nu.mine.mosher.store.Store;
import nu.mine.mosher.util.Props;
import nu.mine.mosher.view.PageHome;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

public class App extends WebApplication {
    @Override
    public Class<? extends WebPage> getHomePage() {
        return PageHome.class;
    }

    public Props props() {
        return this.props;
    }

    public Store store() {
        return this.store;
    }

    private final Store store = new Store(Persona.class.getPackageName());
    private final Props props = new Props(store);
}

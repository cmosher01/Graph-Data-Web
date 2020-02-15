package nu.mine.mosher.graph.datawebapp;

import nu.mine.mosher.graph.datawebapp.store.Store;
import nu.mine.mosher.graph.datawebapp.util.Props;
import nu.mine.mosher.graph.datawebapp.view.PageHome;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

public class GraphDataWebApp extends WebApplication {
    private Store store;
    private Props props;

    @Override
    public Class<? extends WebPage> getHomePage() {
        return PageHome.class;
    }

    @Override
    protected void init() {
        super.init();

        String csvPackageNames = getInitParameter("packages");
        this.store = new Store(csvPackageNames.split(","));
        this.props = new Props(this.store);
    }

    public Props props() {
        return this.props;
    }

    public Store store() {
        return this.store;
    }
}

package nu.mine.mosher.graph.datawebapp.view;

import nu.mine.mosher.graph.datawebapp.util.Utils;
import org.apache.wicket.authroles.authentication.*;

public abstract class SecurePage extends BasePage {
    @Override
    protected void onConfigure() {
        super.onConfigure();

        final AuthenticatedWebApplication app = Utils.app();
        if(!AuthenticatedWebSession.get().isSignedIn()) {
            app.restartResponseAtSignInPage();
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

//        add(new BookmarkablePageLink<Void>("goToHomePage", getApplication().getHomePage()));
//
//        add(new Link<Void>("logOut") {
//
//            @Override
//            public void onClick() {
//                AuthenticatedWebSession.get().invalidate();
//                setResponsePage(getApplication().getHomePage());
//            }
//        });
    }
}

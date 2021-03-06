package nu.mine.mosher.graph.datawebapp.view;

import org.apache.wicket.Application;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.string.Strings;

public class PageLogin extends BasePage {
    private String username;
    private String password;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final StatelessForm<PageLogin> form = new StatelessForm<>("form"){
            @Override
            protected void onSubmit() {
                if (Strings.isEmpty(username) || Strings.isEmpty(password)) {
                    return;
                }

                final boolean authResult = AuthenticatedWebSession.get().signIn(username, password);

                if (authResult) {
                    continueToOriginalDestination();
                    setResponsePage(Application.get().getHomePage());
                }
            }
        };

        form.setDefaultModel(new CompoundPropertyModel<>(this));

        form.add(new TextField<>("username"));
        form.add(new PasswordTextField("password"));

        add(form);
    }
}

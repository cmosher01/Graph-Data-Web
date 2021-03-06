package nu.mine.mosher.graph.datawebapp.view;

import nu.mine.mosher.graph.datawebapp.util.Utils;
import org.apache.wicket.Application;
import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.head.*;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import java.time.*;

public abstract class BasePage extends WebPage {
    public BasePage() {
        add(new Label("title", Utils.app().title()));
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

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        if (!Utils.app().stylesheet().isEmpty()) {
            response.render(CssHeaderItem.forUrl(Utils.app().stylesheet()));
        }
    }

}

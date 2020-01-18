package nu.mine.mosher.view;

import org.apache.wicket.Application;
import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BasePage extends WebPage {
    public BasePage() {
        add(new Link<Void>("home") {
            @Override
            public void onClick() {
                setResponsePage(Application.get().getHomePage());
            }
        });
        add(new Label("timeStamp", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date())));
    }

    @Override
    public MarkupType getMarkupType() {
        return new MarkupType(".xml", MarkupType.XML_MIME);
    }
}

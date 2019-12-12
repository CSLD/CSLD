package cz.larpovadatabaze.calendar.component.panel;

import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.components.common.social.SocialShareButtons;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.ListGamePage;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 *
 */
public class DetailedEventPanel extends Panel {
    public DetailedEventPanel(String id, IModel<Event> model) {
        super(id);

        setDefaultModel(new CompoundPropertyModel<>(model));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Event event = (Event) getDefaultModelObject();
        // Web link
        add(new ExternalLink("webGameLink", Model.of(event.getWeb()), Model.of(event.getWeb())).setVisible(StringUtils.isNotBlank(event.getWeb())));

        add(new Label("name"));
        add(new Label("description").setEscapeModelStrings(false));
        add(new Label("loc"));
        add(new Label("fromCzech"));
        add(new Label("toCzech"));
        add(new Label("amountOfPlayers"));

        // Social share button
        add(new SocialShareButtons("socialShareButtons"));

        // TODO: Move labels to separate component.
        // Labels
        List<cz.larpovadatabaze.entities.Label> labels = event.getLabels();
        ListView<cz.larpovadatabaze.entities.Label> view = new ListView<cz.larpovadatabaze.entities.Label>("labels", labels) {
            @Override
            protected void populateItem(ListItem<cz.larpovadatabaze.entities.Label> item) {
                cz.larpovadatabaze.entities.Label label = item.getModelObject();

                BookmarkablePageLink link = new BookmarkablePageLink<CsldBasePage>("link", ListGamePage.class, ListGamePage.getParametersForLabel(label.getId()));
                item.add(link);
                if (StringUtils.isNotEmpty(label.getDescription())) {
                    link.add(new AttributeAppender("title", label.getDescription()));
                }

                Label labelC = new Label("label", label.getName());
                link.add(labelC);
            }
        };
        add(view);
    }
}

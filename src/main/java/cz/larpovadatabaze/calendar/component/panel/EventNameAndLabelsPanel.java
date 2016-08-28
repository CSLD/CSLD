package cz.larpovadatabaze.calendar.component.panel;

import cz.larpovadatabaze.calendar.component.page.DetailOfEventPage;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.Game;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Created by jbalhar on 28. 8. 2016.
 */
public class EventNameAndLabelsPanel extends AbstractCsldPanel<Event> {
    public EventNameAndLabelsPanel(String id, IModel<Event> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final BookmarkablePageLink<CsldBasePage> eventLink =
                new BookmarkablePageLink<CsldBasePage>("eventLink", DetailOfEventPage.class, DetailOfEventPage.pageParameters(getModelObject()));
        final Label eventName = new Label("eventName", Model.of(getModelObject().getName()));
        eventLink.add(eventName);
        add(eventLink);

        StringBuilder labels = new StringBuilder();
        for(cz.larpovadatabaze.entities.Label l : getModelObject().getLabels()) {
            if (labels.length() > 0) {
                // Add divisor
                labels.append(", ");
            }
            labels.append(l.getName());
        }
        add(new Label("labels", labels.toString()));

    }
}

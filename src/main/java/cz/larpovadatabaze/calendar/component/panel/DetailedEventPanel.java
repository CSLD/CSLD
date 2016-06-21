package cz.larpovadatabaze.calendar.component.panel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 *
 */
public class DetailedEventPanel extends Panel {
    public DetailedEventPanel(String id, IModel<?> model) {
        super(id);

        setDefaultModel(new CompoundPropertyModel<Object>(model));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new Label("name"));
        add(new Label("description").setEscapeModelStrings(false));
        add(new Label("loc"));
        add(new Label("fromCzech"));
        add(new Label("toCzech"));
        add(new Label("amountOfPlayers"));
    }
}

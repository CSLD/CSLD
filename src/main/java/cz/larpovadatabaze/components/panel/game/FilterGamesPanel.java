package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.form.FilterForm;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 2.5.13
 * Time: 22:00
 */
public class FilterGamesPanel extends Panel {
    public FilterGamesPanel(String id) {
        super(id);

        add(new FilterForm("filterForm"));
    }

    protected void onConfigure() {
        setVisibilityAllowed(false);
    }
}

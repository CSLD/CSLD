package cz.larpovadatabaze.components.panel.news;

import org.apache.wicket.markup.html.panel.Panel;

/**
 * This panel can be used to produce new News. At the current stage it is just stored in the database and available
 * as news.
 */
public class CreateNewsPanel extends Panel {
    public CreateNewsPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }
}

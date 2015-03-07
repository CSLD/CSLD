package cz.larpovadatabaze.components.panel.home;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.template.PackageTextTemplate;

/**
 * Panel for upcoming events from larp.cz
 *
 * User: Michal Kara Date: 7.3.15 Time: 21:19
 */
public class LarpCzCalendarPanel extends Panel {
    public LarpCzCalendarPanel(String id) {
        super(id);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        // Render javascript to get items from larp.cz
        response.render(OnDomReadyHeaderItem.forScript(new PackageTextTemplate(getClass(), "LarpCzCalendarPanel.js").getString()));
    }
}

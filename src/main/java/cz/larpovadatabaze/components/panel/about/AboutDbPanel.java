package cz.larpovadatabaze.components.panel.about;

import cz.larpovadatabaze.components.panel.author.AddAuthorPanel;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 29.4.13
 * Time: 12:00
 */
public class AboutDbPanel extends Panel
{
    public AboutDbPanel(String id) {
        super(id);

        add(new AdministratorPanel("administrators"));
        add(new EditorPanel("moderators"));
    }
}

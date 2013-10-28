package cz.larpovadatabaze.components.panel.about;

import org.apache.wicket.markup.html.panel.Panel;

/**
 *  Panel combining together Info about Administrators with info about Editors.
 *  It also contains basic contact info.
 */
public class AboutDbPanel extends Panel
{
    public AboutDbPanel(String id) {
        super(id);

        add(new AdministratorPanel("administrators"));
        add(new EditorPanel("moderators"));
    }
}

package cz.larpovadatabaze.components.page.group;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.group.CreateOrUpdateGroupPanel;

/**
 *
 */
public class AddGroupPage extends CsldBasePage {
    public AddGroupPage(){
        add(new CreateOrUpdateGroupPanel("addGroup", null));
    }
}

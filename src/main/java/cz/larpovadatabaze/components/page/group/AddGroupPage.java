package cz.larpovadatabaze.components.page.group;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.group.CreateOrUpdateGroupPanel;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 *
 */
@AuthorizeInstantiation({"User","Editor","Admin"})
public class AddGroupPage extends CsldBasePage {
    public AddGroupPage(){
        add(new CreateOrUpdateGroupPanel("addGroup", null));
    }
}

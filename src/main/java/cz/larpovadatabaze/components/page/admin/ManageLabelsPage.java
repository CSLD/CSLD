package cz.larpovadatabaze.components.page.admin;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.admin.ManageLabelsPanel;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 */
@AuthorizeInstantiation({"Editor","Admin"})
public class ManageLabelsPage extends CsldBasePage {
    public ManageLabelsPage() {
        Panel manageLabels= new ManageLabelsPanel("manageLabels");
        manageLabels.setOutputMarkupId(true);
        add(manageLabels);
    }
}

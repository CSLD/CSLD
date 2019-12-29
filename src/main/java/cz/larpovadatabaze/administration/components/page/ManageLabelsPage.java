package cz.larpovadatabaze.administration.components.page;

import cz.larpovadatabaze.administration.components.panel.ManageLabelsPanel;
import cz.larpovadatabaze.common.components.page.CsldBasePage;
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

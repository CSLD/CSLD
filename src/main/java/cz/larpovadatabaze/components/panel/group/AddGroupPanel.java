package cz.larpovadatabaze.components.panel.group;

import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 2.5.13
 * Time: 21:08
 */
public class AddGroupPanel extends Panel {
    public AddGroupPanel(String id) {
        super(id);
    }

    public AddGroupPanel(String id, IModel<CsldGroup> model){
        super(id, model);
    }

    protected void onConfigure() {
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isSignedIn());
    }
}

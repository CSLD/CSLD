package cz.larpovadatabaze.components.common;

import cz.larpovadatabaze.security.CsldRoles;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;

/**
 * This Checkbox is visible based on the Role of the Person which is logged in the system.
 */
public class RoleBasedCheckbox extends CheckBox {
    private List<CsldRoles> haveRights;

    public RoleBasedCheckbox(String id, IModel<Boolean> model, List<CsldRoles> haveRightToAccess) {
        super(id, model);
        haveRights = haveRightToAccess;
    }

    @Override
    protected void onConfigure() {
        CsldRoles actualRole = getRoleOfActualUser();
        if(!haveRights.contains(actualRole)){
            setVisibilityAllowed(false);
        }
        super.onConfigure();
    }

    public CsldRoles getRoleOfActualUser() {
        return CsldRoles.ANONYMOUS;
    }
}

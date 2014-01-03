package cz.larpovadatabaze.components.common.icons;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUserService;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: Michal Kara
 * Date: 3.1.14
 * Time: 15:44
 */
public class UserIcon extends AbstractCsldIcon<CsldUserService, CsldUser> {

    @SpringBean
    private CsldUserService userService;

    public UserIcon(String id, IModel<CsldUser> model) {
        super(id, model);
    }

    @Override
    public CsldUserService getService() {
        return userService;
    }
}

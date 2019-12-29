package cz.larpovadatabaze.users.components.icons;

import cz.larpovadatabaze.common.components.icons.AbstractCsldIcon;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: Michal Kara
 * Date: 3.1.14
 * Time: 15:44
 */
public class UserIcon extends AbstractCsldIcon<CsldUsers, CsldUser> {

    @SpringBean
    private CsldUsers userService;

    public UserIcon(String id, IModel<CsldUser> model) {
        super(id, model);
    }

    @Override
    public CsldUsers getService() {
        return userService;
    }
}

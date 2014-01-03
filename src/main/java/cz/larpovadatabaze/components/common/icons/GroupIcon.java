package cz.larpovadatabaze.components.common.icons;

import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.services.GroupService;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: Michal Kara
 * Date: 3.1.14
 * Time: 15:56
 */
public class GroupIcon extends AbstractCsldIcon<GroupService, CsldGroup> {
    @SpringBean
    private GroupService groupService;

    public GroupIcon(String id, IModel<CsldGroup> model) {
        super(id, model);
    }

    @Override
    protected GroupService getService() {
        return groupService;
    }
}

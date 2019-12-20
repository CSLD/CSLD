package cz.larpovadatabaze.components.common.icons;

import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.services.CsldGroups;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: Michal Kara
 * Date: 3.1.14
 * Time: 15:56
 */
public class GroupIcon extends AbstractCsldIcon<CsldGroups, CsldGroup> {
    @SpringBean
    private CsldGroups csldGroups;

    public GroupIcon(String id, IModel<CsldGroup> model) {
        super(id, model);
    }

    @Override
    protected CsldGroups getService() {
        return csldGroups;
    }
}

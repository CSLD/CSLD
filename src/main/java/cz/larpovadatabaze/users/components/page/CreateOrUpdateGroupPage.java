package cz.larpovadatabaze.users.components.page;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.components.page.HomePage;
import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.utils.HbUtils;
import cz.larpovadatabaze.users.components.panel.CreateOrUpdateGroupPanel;
import cz.larpovadatabaze.users.services.CsldGroups;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 *  This Page allows user to create new or edit existing group.
 */
@AuthorizeInstantiation({"User","Editor","Admin"})
public class CreateOrUpdateGroupPage extends CsldBasePage {
    @SpringBean
    CsldGroups csldGroups;

    public CreateOrUpdateGroupPage(PageParameters params){
        CsldGroup group = null;
        if(!params.isEmpty()){
            Integer id = params.get("id").to(Integer.class);
            group = csldGroups.getById(id);
            if(HbUtils.isProxy(group)){
                group = HbUtils.deproxy(group);
            }
        }

        add(new CreateOrUpdateGroupPanel("addGroup", group){
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Object object) {
                super.onCsldAction(target, object);

                throw new RestartResponseException(HomePage.class);
            }
        });
    }
}

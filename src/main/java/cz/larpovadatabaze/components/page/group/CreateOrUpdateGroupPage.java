package cz.larpovadatabaze.components.page.group;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.group.CreateOrUpdateGroupPanel;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.services.GroupService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 *  This Page allows user to create new or edit existing group.
 */
@AuthorizeInstantiation({"User","Editor","Admin"})
public class CreateOrUpdateGroupPage extends CsldBasePage {
    @SpringBean
    GroupService groupService;

    public CreateOrUpdateGroupPage(PageParameters params){
        CsldGroup group = null;
        if(!params.isEmpty()){
            Integer id = params.get("id").to(Integer.class);
            group = groupService.getById(id);
            if(HbUtils.isProxy(group)){
                group = HbUtils.deproxy(group);
            }
        }

        add(new CreateOrUpdateGroupPanel("addGroup", group){
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                super.onCsldAction(target, form);

                throw new RestartResponseException(ListGroup.class);
            }
        });
    }
}

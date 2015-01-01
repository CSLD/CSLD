package cz.larpovadatabaze.components.page.group;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.group.AdministerGroupMembers;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import cz.larpovadatabaze.services.GroupService;

/**
 * This Page contains panels which has anything to do with managing groups members.
 */
@AuthorizeInstantiation({"User","Editor","Admin"})
public class ManageGroupPage extends CsldBasePage {
    @SpringBean
    GroupService groupService;

    private class GroupModel extends LoadableDetachableModel<CsldGroup> {
        private final Integer id;

        private GroupModel(Integer id) {
            this.id = id;
        }

        @Override
        protected CsldGroup load() {
            return groupService.getById(id);
        }
    }

    public ManageGroupPage(PageParameters params){
        Integer groupId = params.get("id").to(Integer.class);
        setDefaultModel(new GroupModel(groupId));

        boolean isVisible = CsldAuthenticatedWebSession.get().isSignedIn();
        if(isVisible){
            CsldUser logged = CsldAuthenticatedWebSession.get().getLoggedUser();
            if(logged.getRole() <= CsldRoles.USER.getRole()){
                if(!((CsldGroup)getDefaultModelObject()).getAdministrators().contains(logged)){
                    isVisible = false;
                }
            }
        }
        if(!isVisible) {
            throw new RestartResponseException(ListGroup.class);
        }

        add(new AdministerGroupMembers("manageGroup", (IModel<CsldGroup>)getDefaultModel()) {
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                PageParameters params = new PageParameters();
                params.add("id", ((CsldGroup)getDefaultModelObject()).getId());

                throw new RestartResponseException(GroupDetail.class, params);
            }
        });
    }
}

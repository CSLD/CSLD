package cz.larpovadatabaze.users.components.page;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.components.page.HomePage;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.utils.HbUtils;
import cz.larpovadatabaze.users.components.panel.CreateOrUpdateAuthorPanel;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 *  This page allows user to create new or edit existing user.
 */
@AuthorizeInstantiation({"User","Editor","Admin"})
public class CreateOrUpdateAuthorPage extends CsldBasePage {
    @SpringBean
    CsldUsers csldUsers;

    public CreateOrUpdateAuthorPage(PageParameters params){
        CsldUser csldUser  = null;
        if(!params.isEmpty()){
            Integer id = params.get("id").to(Integer.class);
            csldUser = csldUsers.getById(id);
            if(HbUtils.isProxy(csldUser)){
                csldUser = HbUtils.deproxy(csldUser);
            }
        }

        add(new CreateOrUpdateAuthorPanel("createOrUpdateAuthor", csldUser != null ? csldUser : CsldUser.getEmptyUser()){
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Object object) {
                super.onCsldAction(target, object);

                throw new RestartResponseException(HomePage.class);
            }
        });
    }
}

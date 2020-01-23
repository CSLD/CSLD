package cz.larpovadatabaze.users.components.page;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import cz.larpovadatabaze.common.components.page.HomePage;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.users.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.users.components.panel.CreateOrUpdateUserPanel;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * This page allows user to create new exiting user.
 */
public class CreateUserPage extends CsldBasePage {
    @SpringBean
    CsldUsers csldUsers;

    public CreateUserPage(PageParameters params){
        if ((CsldAuthenticatedWebSession.get()).isSignedIn()) {
            // Cannot create when logged in
            throw new RestartResponseException(HomePage.class);
        }


        CsldUser csldUser  = null;
        final boolean isNew = (csldUser == null);

        add(new CreateOrUpdateUserPanel("createOrUpdateUser", csldUser){
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Object object) {
                super.onCsldAction(target, object);

                if(!isNew) {
                    CsldUser user = (CsldUser) object;
                    PageParameters params = new PageParameters();
                    params.add("id", user.getId());

                    throw new RestartResponseException(UserDetailPage.class, params);
                } else {
                    throw new RestartResponseException(HomePage.class);
                }
            }
        });
    }
}
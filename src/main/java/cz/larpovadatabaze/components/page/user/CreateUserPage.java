package cz.larpovadatabaze.components.page.user;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.components.panel.user.CreateOrUpdateUserPanel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.CsldUserService;

/**
 * This page allows user to create new exiting user.
 */
public class CreateUserPage extends CsldBasePage {
    @SpringBean
    CsldUserService csldUserService;

    public CreateUserPage(PageParameters params){
        if ((CsldAuthenticatedWebSession.get()).isSignedIn()) {
            // Cannot create when logged in
            throw new RestartResponseException(HomePage.class);
        }


        CsldUser csldUser  = null;
        final boolean isNew = (csldUser == null);

        add(new CreateOrUpdateUserPanel("createOrUpdateUser", csldUser){
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                super.onCsldAction(target, form);

                if(!isNew) {
                    CsldUser user = (CsldUser) form.getModelObject();
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

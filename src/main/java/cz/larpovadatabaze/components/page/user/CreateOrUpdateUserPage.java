package cz.larpovadatabaze.components.page.user;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.components.panel.user.CreateOrUpdateUserPanel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * This page allows user to create new or update exiting user.
 */
public class CreateOrUpdateUserPage extends CsldBasePage {
    @SpringBean
    CsldUserService csldUserService;

    public CreateOrUpdateUserPage(PageParameters params){
        Image settingsIcon = new Image("settingsIcon",
                new PackageResourceReference(Csld.class, cz.larpovadatabaze.entities.Image.getSettingsIconPath()));
        add(settingsIcon);

        CsldUser csldUser  = null;
        if(!params.isEmpty()){
            Integer id = params.get("id").to(Integer.class);
            CsldUser logged = ((CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get()).getLoggedUser();
            if(logged.getId().intValue() == id){
                csldUser = logged;
            } else {
                csldUser = csldUserService.getById(id);
                if(HbUtils.isProxy(csldUser)){
                    csldUser = HbUtils.deproxy(csldUser);
                }
            }
        }
        final boolean isNew = (csldUser == null);

        add(new CreateOrUpdateUserPanel("createOrUpdateUser", csldUser){
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                super.onCsldAction(target, form);

                if(!isNew) {
                    CsldUser user = (CsldUser) form.getModelObject();
                    PageParameters params = new PageParameters();
                    params.add("id", user.getId());

                    throw new RestartResponseException(UserDetail.class, params);
                } else {
                    throw new RestartResponseException(HomePage.class);
                }
            }
        });
    }
}

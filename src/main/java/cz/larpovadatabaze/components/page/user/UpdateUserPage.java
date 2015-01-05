package cz.larpovadatabaze.components.page.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.components.panel.user.UpdateUserPanel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 *
 */
public class UpdateUserPage extends CsldBasePage {
    @SpringBean
    CsldUserService csldUserService;

    public UpdateUserPage(PageParameters params){
        CsldUser csldUser  = null;
        if(!params.isEmpty()){
            Integer id = params.get("id").to(Integer.class);
            CsldUser logged = CsldAuthenticatedWebSession.get().getLoggedUser();
            if(logged.getId().intValue() != id){
                throw new RestartResponseException(HomePage.class);
            }
            csldUser = csldUserService.getById(id);
            if(HbUtils.isProxy(csldUser)){
                csldUser = HbUtils.deproxy(csldUser);
            }
        }
        final boolean isNew = (csldUser == null);

        add(new UpdateUserPanel("updateUser", csldUser){
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

package cz.larpovadatabaze.components.page.author;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.components.panel.author.CreateOrUpdateAuthorPanel;
import cz.larpovadatabaze.components.panel.user.CreateOrUpdateUserPanel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 *  This page allows user to create new or edit existing user.
 */
@AuthorizeInstantiation({"User","Editor","Admin"})
public class CreateOrUpdateAuthorPage extends CsldBasePage {
    @SpringBean
    private CsldUserService csldUserService;

    public CreateOrUpdateAuthorPage(PageParameters params){
        CsldUser csldUser  = null;
        if(!params.isEmpty()){
            Integer id = params.get("id").to(Integer.class);
            csldUser = csldUserService.getById(id);
            if(HbUtils.isProxy(csldUser)){
                csldUser = HbUtils.deproxy(csldUser);
            }
        }

        add(new CreateOrUpdateAuthorPanel("createOrUpdateAuthor", csldUser != null ? csldUser.getPerson() : Person.getEmptyPerson()){
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                super.onCsldAction(target, form);

                Person author = (Person) form.getModelObject();
                PageParameters params = new PageParameters();
                params.add("id", author.getId());

                throw new RestartResponseException(AuthorDetail.class, params);
            }
        });
    }
}

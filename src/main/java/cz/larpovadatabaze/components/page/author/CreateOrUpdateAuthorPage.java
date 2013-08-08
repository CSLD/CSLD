package cz.larpovadatabaze.components.page.author;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.author.CreateOrUpdateAuthorPanel;
import cz.larpovadatabaze.components.panel.user.CreateOrUpdateUserPanel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 *
 */
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

        add(new CreateOrUpdateAuthorPanel("createOrUpdateAuthor", csldUser != null ? csldUser.getPerson() : Person.getEmptyPerson()));
    }
}

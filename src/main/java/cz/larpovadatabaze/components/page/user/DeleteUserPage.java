package cz.larpovadatabaze.components.page.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.HomePage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUsers;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 15.5.13
 * Time: 13:42
 *
 * TODO - currently not used - TODO
 */
@SuppressWarnings("unused")
public class DeleteUserPage extends CsldBasePage {
    @SpringBean
    CsldUsers csldUsers;

    public DeleteUserPage(PageParameters params){
        Integer userId = params.get("id").to(Integer.class);

        CsldUser csldUser = csldUsers.getById(userId);
        if(csldUser != null) {
            csldUsers.remove(csldUser);
        }

        throw new RestartResponseException(HomePage.class);
    }
}

package cz.larpovadatabaze.components.page.user;

import cz.larpovadatabaze.components.form.RegisterForm;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 26.4.13
 * Time: 9:21
 */
public class RegisterUserPage extends CsldBasePage {
    @SpringBean
    private CsldUserService csldUserService;

    public RegisterUserPage(PageParameters params){
        CsldUser csldUser  = null;
        if(!params.isEmpty()){
            Integer id = params.get("id").to(Integer.class);
            csldUser = csldUserService.getById(id);
            if(HbUtils.isProxy(csldUser)){
                csldUser = HbUtils.deproxy(csldUser);
            }
        }
        if(csldUser == null){
            csldUser = new CsldUser();
        }

        add(new RegisterForm("addUser", csldUser));
    }
}

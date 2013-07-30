package cz.larpovadatabaze.components.page.group;

import cz.larpovadatabaze.components.form.AddGroupForm;
import cz.larpovadatabaze.components.page.CsldBasePage;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 17.4.13
 * Time: 18:14
 */
public class AddGroupPage extends CsldBasePage {
    public AddGroupPage(){
        add(new AddGroupForm("addGroup"));
    }
}

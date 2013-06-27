package cz.larpovadatabaze.components.page.add;

import cz.larpovadatabaze.components.form.AddPersonForm;
import cz.larpovadatabaze.components.page.CsldBasePage;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 11.4.13
 * Time: 15:36
 */
public class AddPersonPage extends CsldBasePage {
    public AddPersonPage(){
        add(new AddPersonForm("addPerson"));
    }
}

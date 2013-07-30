package cz.larpovadatabaze.components.page.about;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.about.AboutDbPanel;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 28.4.13
 * Time: 19:15
 */
public class AboutDatabase extends CsldBasePage {
    public AboutDatabase(){
        add(new AboutDbPanel("rightPartAboutDb"));
    }
}

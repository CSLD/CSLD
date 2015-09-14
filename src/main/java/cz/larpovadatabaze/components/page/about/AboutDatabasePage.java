package cz.larpovadatabaze.components.page.about;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.about.AboutDbPanel;

/**
 *
 */
public class AboutDatabasePage extends CsldBasePage {
    public AboutDatabasePage(){
        add(new JQueryBehavior("#accordion","accordion"));

        add(new AboutDbPanel("rightPartAboutDb"));
    }
}

package cz.larpovadatabaze.components.page.about;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.about.AboutDbPanel;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.ContextRelativeResource;

/**
 *
 */
public class AboutDatabase extends CsldBasePage {
    public AboutDatabase(){
        Image questionImage = new Image("questionIcon", new ContextRelativeResource(cz.larpovadatabaze.entities.Image.getQuestionIconPath()));
        add(questionImage);

        add(new JQueryBehavior("#accordion","accordion"));

        add(new AboutDbPanel("rightPartAboutDb"));
    }
}

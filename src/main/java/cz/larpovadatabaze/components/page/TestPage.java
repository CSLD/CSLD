package cz.larpovadatabaze.components.page;

import cz.larpovadatabaze.components.panel.ImagePanel;
import cz.larpovadatabaze.entities.Image;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;

/**
 *
 */
public class TestPage extends CsldBasePage {
    public TestPage(){
        Label label = new Label("test", Model.of("Pom"));
        label.add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                System.out.println("Clicked");
            }
        });
        add(label);
    }
}

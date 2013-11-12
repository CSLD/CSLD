package cz.larpovadatabaze.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;

public class ErrorClassAppender extends Behavior
{
    @Override
    public void onComponentTag(Component component, ComponentTag
            tag) {
        if (((FormComponent<?>) component).isValid() == false) {
            String cl = tag.getAttribute("class");
            if (cl == null) {
                tag.put("class", "error");
            } else {
                tag.put("class", "error " + cl);
            }
        }
    }
}
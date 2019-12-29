package cz.larpovadatabaze.common.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;

public class ErrorClassAppender extends Behavior
{
    @Override
    public void onComponentTag(Component component, ComponentTag
            tag) {
        final String className = "class";
        if (!((FormComponent<?>) component).isValid()) {
            String cl = tag.getAttribute(className);
            if (cl == null) {
                tag.put(className, "error");
            } else {
                tag.put(className, "error " + cl);
            }
        }
    }
}
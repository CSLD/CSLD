package cz.larpovadatabaze.calendar.component.common;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

/**
 * Wrapper around TextField for time input field.
 */
public class TimeTextField extends TextField<String> {
    public TimeTextField(String id, IModel<String> model) {
        super(id, model);
    }

    protected String getInputType() {
        return "time";
    }
}

package cz.larpovadatabaze.calendar.component.common;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

/**
 * Wrapper around TextField for time input field.
 */
public class TimeTextField extends TextField{
    public TimeTextField(String id) {
        super(id);
    }

    public TimeTextField(String id, Class type) {
        super(id, type);
    }

    public TimeTextField(String id, IModel model) {
        super(id, model);
    }

    public TimeTextField(String id, IModel model, Class type) {
        super(id, model, type);
    }

    protected String getInputType() {
        return "time";
    }
}

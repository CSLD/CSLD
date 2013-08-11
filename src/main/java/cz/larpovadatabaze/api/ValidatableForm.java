package cz.larpovadatabaze.api;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

/**
 * This form provides to public validation of itself.
 */
public class ValidatableForm<T> extends Form<T> {
    public ValidatableForm(String id) {
        super(id);
    }

    public ValidatableForm(String id, IModel<T> model) {
        super(id, model);
    }

    public boolean isValid(){
        validate();

        return !hasError();
    }
}
